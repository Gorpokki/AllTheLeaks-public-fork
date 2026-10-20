package dev.uncandango.alltheleaks.utils;

import dev.uncandango.alltheleaks.AllTheLeaks;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class MethodNodeDebug {
   public static void printNormalizedInstructions(MethodNode method) {
      Map<LabelNode, Integer> labelIds = new IdentityHashMap<>();
      AllTheLeaks.LOGGER.info("Method: {}{}", method.name, method.desc);
      AllTheLeaks.LOGGER.info("Access: {}", method.access);
      AllTheLeaks.LOGGER.info("Instructions:");
      int index = 0;
      Function<LabelNode, Integer> getLabelId = lx -> {
         Integer id = labelIds.get(lx);
         if (id == null) {
            int newId = labelIds.size();
            labelIds.put(lx, newId);
            return newId;
         } else {
            return id;
         }
      };

      for (AbstractInsnNode insn : method.instructions) {
         if (!(insn instanceof FrameNode) && !(insn instanceof LineNumberNode)) {
            if (insn instanceof LabelNode l) {
               int id = getLabelId.apply(l);
               System.out.printf("  %03d: LABEL_%d%n", index++, id);
            } else {
               StringBuilder sb = new StringBuilder();
               sb.append(String.format("  %03d: %-15s", index++, insn.getClass().getSimpleName()));
               sb.append(" opcode=").append(insn.getOpcode());
               switch (insn.getType()) {
                  case 1:
                     sb.append(" operand=").append(((IntInsnNode)insn).operand);
                     break;
                  case 2:
                     sb.append(" var=").append(((VarInsnNode)insn).var);
                     break;
                  case 3:
                     sb.append(" desc=").append(((TypeInsnNode)insn).desc);
                     break;
                  case 4:
                     FieldInsnNode f = (FieldInsnNode)insn;
                     sb.append(" ").append(f.owner).append(".").append(f.name).append(":").append(f.desc);
                     break;
                  case 5: {
                     MethodInsnNode m = (MethodInsnNode)insn;
                     sb.append(" ").append(m.owner).append(".").append(m.name).append(m.desc);
                     sb.append(" itf=").append(m.itf);
                     break;
                  }
                  case 6:
                     InvokeDynamicInsnNode indy = (InvokeDynamicInsnNode)insn;
                     sb.append(" indy name=").append(indy.name).append(" desc=").append(indy.desc);
                     sb.append(" bsm=").append(indy.bsm);
                     sb.append(" args=").append(Arrays.toString(indy.bsmArgs));
                     break;
                  case 7:
                     JumpInsnNode j = (JumpInsnNode)insn;
                     int id = getLabelId.apply(j.label);
                     sb.append(" -> LABEL_").append(id);
                  case 8:
                  default:
                     break;
                  case 9:
                     Object cst = ((LdcInsnNode)insn).cst;
                     if (cst instanceof ConstantDynamic cd) {
                        sb.append(" ldc(ConstantDynamic) name=")
                           .append(cd.getName())
                           .append(" desc=")
                           .append(cd.getDescriptor())
                           .append(" bsm=")
                           .append(cd.getBootstrapMethod());
                     } else {
                        sb.append(" ldc=").append(cst);
                     }
                     break;
                  case 10:
                     IincInsnNode iinc = (IincInsnNode)insn;
                     sb.append(" var=").append(iinc.var).append(" incr=").append(iinc.incr);
                     break;
                  case 11:
                     TableSwitchInsnNode tsi = (TableSwitchInsnNode)insn;
                     sb.append(" min=").append(tsi.min).append(" max=").append(tsi.max);
                     int defId = getLabelId.apply(tsi.dflt);
                     sb.append(" default=LABEL_").append(defId).append(" labels=[");

                     for (LabelNode l : tsi.labels) {
                        int lid = getLabelId.apply(l);
                        sb.append("LABEL_").append(lid).append(" ");
                     }

                     sb.append("]");
                     break;
                  case 12:
                     LookupSwitchInsnNode lsi = (LookupSwitchInsnNode)insn;
                     int dfltId = getLabelId.apply(lsi.dflt);
                     sb.append(" default=LABEL_").append(dfltId);
                     sb.append(" keys/labels=[");

                     for (int i = 0; i < lsi.keys.size(); i++) {
                        int lid = getLabelId.apply((LabelNode)lsi.labels.get(i));
                        sb.append(lsi.keys.get(i)).append("->LABEL_").append(lid).append(" ");
                     }

                     sb.append("]");
                     break;
                  case 13: {
                     MultiANewArrayInsnNode m = (MultiANewArrayInsnNode)insn;
                     sb.append(" desc=").append(m.desc).append(" dims=").append(m.dims);
                  }
               }

               AllTheLeaks.LOGGER.info(sb.toString());
            }
         }
      }
   }
}
