#!/bin/bash

# Script para probar endpoints con RawMention

URL="${1:-http://51.159.58.3:8888/api/segmentation-publications}"
LIMIT="${2:-100}"

echo "=== Prueba búsqueda vectorial sin embeddings ==="
echo "Consultando: ${URL}?query=test&limit=${LIMIT}"
echo "Contando registros..."

COUNT=$(curl -s "${URL}?query=test&limit=${LIMIT}" | jq '.publications | length')
echo "Registros recibidos sin embeddings: ${COUNT}"

echo ""
echo "=== Prueba búsqueda vectorial con embeddings ==="
echo "Consultando: ${URL}?query=test&limit=${LIMIT}&withEmbeddings=true"
echo "Contando registros con embeddings..."

COUNT=$(curl -s "${URL}?query=test&limit=${LIMIT}&withEmbeddings=true" | jq '.publications | length')
echo "Registros recibidos con embeddings: ${COUNT}"

echo ""
echo "=== Prueba búsqueda por fecha (MongoDB directo) ==="
DATE_URL="${URL}/by-date"
echo "Consultando: ${DATE_URL}?from=2025-11-01T00:00:00Z&to=2026-01-24T00:00:00Z&limit=${LIMIT}"
echo "Contando registros..."

COUNT=$(curl -s "${DATE_URL}?from=2025-11-01T00:00:00Z&to=2026-01-24T00:00:00Z&limit=${LIMIT}" | jq 'length')
echo "Registros recibidos por fecha: ${COUNT}"
