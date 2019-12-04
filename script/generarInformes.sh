#!/usr/bin/env bash
pandoc preambulo.md informe.md --pdf-engine=xelatex -o informe.pdf --highlight-style tango
pandoc preambulo.md informe.md -s -o informe.tex
