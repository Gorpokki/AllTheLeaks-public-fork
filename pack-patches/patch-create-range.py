"""Build the Create 6.0.10 override: python3 patch-create-range.py INPUT.jar OUTPUT.jar.

Input: https://edge.forgecdn.net/files/8943/912/alltheleaks-1.1.13+1.21.1-neoforge.jar
Only Issue9858's range changes. The released jar retains fixes absent from public source.
"""

import argparse
import hashlib
from pathlib import Path
import zipfile

INPUT_SHA1 = "ea11d23e0f8c48544a269dc7c56f6593cb1cafe2"
OUTPUT_SHA1 = "ab6d901e0d4fe896666487dc08d7b03f088a751e"
CLASS = "dev/uncandango/alltheleaks/leaks/client/mods/create/Issue9858.class"
OLD = b"[6.0.0,6.0.9]"
NEW = b"[6.0.0,6.0.10]"


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("input", type=Path)
    parser.add_argument("output", type=Path)
    args = parser.parse_args()
    if hashlib.sha1(args.input.read_bytes()).hexdigest() != INPUT_SHA1:
        raise ValueError("Input does not match the released AllTheLeaks 1.1.13 jar")
    old_constant = len(OLD).to_bytes(2, "big") + OLD
    new_constant = len(NEW).to_bytes(2, "big") + NEW
    with zipfile.ZipFile(args.input) as source:
        original = source.read(CLASS)
        if original.count(old_constant) != 1:
            raise ValueError("Expected exactly one Create range constant")
        patched = original.replace(old_constant, new_constant)
        with zipfile.ZipFile(args.output, "x") as target:
            target.comment = source.comment
            for entry in source.infolist():
                target.writestr(entry, patched if entry.filename == CLASS else source.read(entry.filename))
    digest = hashlib.sha1(args.output.read_bytes()).hexdigest()
    if digest != OUTPUT_SHA1:
        raise ValueError(f"Output SHA1 differs: {digest}")
    print(f"{digest}  {args.output.name}")


if __name__ == "__main__":
    main()
