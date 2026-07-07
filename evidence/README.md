# Evidence Files

This directory contains machine-readable / reproducible evidence used by the report.

## Files

```text
javap-simple/
  Raw javap output for every class in KillsaySimple 1.0.0.

jar-tree/simple_classes.txt
  Class list from KillsaySimple 1.0.0.

jar-tree/ltsc_classes.txt
  Class list from KillsayReborn LTSC 2.0.

similarity_report.md
  Experimental class similarity report.
  Treat it as a lead-finding aid, not final proof.
```

## Important Notes

The final report is in:

```text
../paste/README.md
```

The similarity report contains noisy matches because generic Java bytecode shares many tokens. The strongest conclusions in `paste/README.md` were checked manually against refmap, class names, bytecode calls, and constants.

