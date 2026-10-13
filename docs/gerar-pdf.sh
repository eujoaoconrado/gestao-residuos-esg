#!/bin/bash
# Gera o documentacao.pdf a partir do documentacao.md
# Uso: ./gerar-pdf.sh (rodar de dentro da pasta docs)

cd "$(dirname "$0")"

if command -v pdflatex > /dev/null; then
  pandoc documentacao.md -o documentacao.pdf
else
  # sem latex: gera o html e imprime em pdf com o chrome
  pandoc documentacao.md -s --metadata pagetitle="Desafio DevOps - Gestão de Resíduos" -c estilo.css -o documentacao.html
  "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" --headless --disable-gpu --no-pdf-header-footer \
    --print-to-pdf=documentacao.pdf documentacao.html
fi

echo "PDF gerado: documentacao.pdf"
