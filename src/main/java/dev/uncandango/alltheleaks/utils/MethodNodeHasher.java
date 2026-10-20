package dev.uncandango.alltheleaks.utils;

import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.Map;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
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

public class MethodNodeHasher {
   private static final int FNV_PRIME = 16777619;

   public static synchronized int hash(MethodNode method) {
      int hash = -2128831035;
      Map<LabelNode, Integer> labelIds = new IdentityHashMap<>();
      int[] labelCounter = new int[]{0};

      for (AbstractInsnNode insn : method.instructions) {
         if (!(insn instanceof LineNumberNode) && !(insn instanceof FrameNode)) {
            if (insn instanceof LabelNode) {
               labelIds.computeIfAbsent((LabelNode)insn, k -> Integer.valueOf((int)(labelCounter[0]++)));
            } else {
               hash = fnvUpdate(hash, insn.getOpcode());
               switch (insn.getType()) {
                  case 1:
                     hash = fnvUpdate(hash, ((IntInsnNode)insn).operand);
                     break;
                  case 2:
                     hash = fnvUpdate(hash, ((VarInsnNode)insn).var);
                     break;
                  case 3:
                     hash = fnvUpdate(hash, ((TypeInsnNode)insn).desc);
                     break;
                  case 4:
                     FieldInsnNode f = (FieldInsnNode)insn;
                     hash = fnvUpdate(hash, f.owner);
                     hash = fnvUpdate(hash, f.name);
                     hash = fnvUpdate(hash, f.desc);
                     break;
                  case 5:
                     MethodInsnNode m = (MethodInsnNode)insn;
                     hash = fnvUpdate(hash, m.owner);
                     hash = fnvUpdate(hash, m.name);
                     hash = fnvUpdate(hash, m.desc);
                     hash = fnvUpdate(hash, m.itf ? 1 : 0);
                     break;
                  case 6:
                     InvokeDynamicInsnNode indy = (InvokeDynamicInsnNode)insn;
                     hash = fnvUpdate(hash, indy.name);
                     hash = fnvUpdate(hash, indy.desc);
                     hash = fnvUpdate(hash, indy.bsm.toString());

                     for (Object arg : indy.bsmArgs) {
                        hash = fnvUpdate(hash, String.valueOf(arg));
                     }
                     break;
                  case 7:
                     JumpInsnNode j = (JumpInsnNode)insn;
                     int labelId = labelIds.computeIfAbsent(j.label, k -> Integer.valueOf((int)(labelCounter[0]++)));
                     hash = fnvUpdate(hash, labelId);
                  case 8:
                  default:
                     break;
                  case 9:
                     Object cst = ((LdcInsnNode)insn).cst;
                     updateLdcConstant(hash, cst);
                     hash = updateLdcConstant(hash, cst);
                     break;
                  case 10:
                     IincInsnNode iinc = (IincInsnNode)insn;
                     hash = fnvUpdate(hash, iinc.var);
                     hash = fnvUpdate(hash, iinc.incr);
                     break;
                  case 11:
                     TableSwitchInsnNode tsi = (TableSwitchInsnNode)insn;
                     hash = fnvUpdate(hash, tsi.min);
                     hash = fnvUpdate(hash, tsi.max);
                     int defId = labelIds.computeIfAbsent(tsi.dflt, k -> Integer.valueOf((int)(labelCounter[0]++)));
                     hash = fnvUpdate(hash, defId);

                     for (LabelNode l : tsi.labels) {
                        int lid = labelIds.computeIfAbsent(l, k -> Integer.valueOf((int)(labelCounter[0]++)));
                        hash = fnvUpdate(hash, lid);
                     }
                     break;
                  case 12:
                     LookupSwitchInsnNode lsi = (LookupSwitchInsnNode)insn;
                     int dfltId = labelIds.computeIfAbsent(lsi.dflt, k -> Integer.valueOf((int)(labelCounter[0]++)));
                     hash = fnvUpdate(hash, dfltId);

                     for (int i = 0; i < lsi.keys.size(); i++) {
                        hash = fnvUpdate(hash, (Integer)lsi.keys.get(i));
                        int lid = labelIds.computeIfAbsent((LabelNode)lsi.labels.get(i), k -> Integer.valueOf((int)(labelCounter[0]++)));
                        hash = fnvUpdate(hash, lid);
                     }
                     break;
                  case 13:
                     MultiANewArrayInsnNode multi = (MultiANewArrayInsnNode)insn;
                     hash = fnvUpdate(hash, multi.desc);
                     hash = fnvUpdate(hash, multi.dims);
               }
            }
         }
      }

      return hash;
   }

   private static int fnvUpdate(int hash, int value) {
      hash ^= value;
      return hash * 16777619;
   }

   private static int fnvUpdate(int hash, String str) {
      if (str == null) {
         return hash;
      }

      byte[] data = str.getBytes(StandardCharsets.UTF_8);

      for (byte b : data) {
         hash ^= b & 255;
         hash *= 16777619;
      }

      return hash;
   }

   private static int updateLdcConstant(int hash, Object cst) {
      if (cst instanceof ConstantDynamic cd) {
         hash = fnvUpdate(hash, cd.getName());
         hash = fnvUpdate(hash, cd.getDescriptor());
         hash = fnvUpdate(hash, cd.getBootstrapMethod().toString());

         for (int i = 0; i < cd.getBootstrapMethodArgumentCount(); i++) {
            Object arg = cd.getBootstrapMethodArgument(i);
            hash = fnvUpdate(hash, String.valueOf(arg));
         }
      } else {
         hash = fnvUpdate(hash, String.valueOf(cst));
      }

      return hash;
   }

   public static int hash(ClassNode classNode, String methodDesc) {
      for (MethodNode method : classNode.methods) {
         String fullName = method.name + method.desc;
         if (fullName.equals(methodDesc)) {
            return hash(method);
         }
      }

      throw new IllegalArgumentException("Method with descriptor " + methodDesc + " not found!");
   }
}
