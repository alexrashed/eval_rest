# Evaluation of HEATEOAS support by Java frameworks
This repository contains the source code for the evaluation part of my bachelor thesis.

## Build
Plain standard maven install in the evaluation dir: 
```
cd evaluation
mvn clean install
```

## Branching
This branch (test_prep) contains the base test code of evaluation part of the thesis.
It does not contain any regression tests (as they would fail).
The different test branches (rfmm_lvl*) contain the test code for the different REST Framework Maturity Model levels.
This branch is the base branch for those branches.
Any further details can be found in the thesis itself.
