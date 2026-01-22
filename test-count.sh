#!/bin/bash

# Script para probar endpoints con y sin embeddings

URL="${1:-http://51.159.58.3:8888/api/segmentation-publications}"
LIMIT="${2:-100}"

echo "=== Prueba sin embeddings ==="
echo "Consultando: ${URL}?query=test&limit=${LIMIT}"
echo "Contando registros..."

COUNT=$(curl -s "${URL}?query=test&limit=${LIMIT}" | jq '.publications | length')
echo "Registros recibidos sin embeddings: ${COUNT}"

echo ""
echo "=== Prueba con embeddings ==="
echo "Consultando: ${URL}?query=test&limit=${LIMIT}&withEmbeddings=true"
echo "Contando registros con embeddings..."

COUNT=$(curl -s "${URL}?query=test&limit=${LIMIT}&withEmbeddings=true" | jq '.publications | length')
echo "Registros recibidos con embeddings: ${COUNT}"
