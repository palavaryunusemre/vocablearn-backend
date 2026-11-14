#!/usr/bin/env bash
# VocabLearn - sıfır bir cihazda tam kurulum scripti.
#
# Ne yapar:
#   1) vocablearn-backend ve vocablearn-frontend repolarını GitHub'dan klonlar
#      (yan yana klasörlere: ./vocablearn/backend, ./vocablearn/frontend)
#   2) backend/.env dosyasını .env.example'dan oluşturur (yoksa) ve
#      GROQ_API_KEY'i -- script'e argüman olarak ya da ortam değişkeni
#      olarak verildiyse -- otomatik doldurur.
#   3) Postgres container'ını ayağa kaldırır, sağlıklı olmasını bekler.
#   4) Veritabanı boşsa VE backend/db-backup/vocablearn_backup.sql.gz elden
#      kopyalanmışsa (bu dosya repoya dahil DEĞİLDİR -- gerçek kullanıcı
#      verisi içerdiği için asla git'e girmez) onu restore eder. Yoksa
#      backend kendi seeder'larıyla (parça/bağlaç/modal/quiz içeriği)
#      zaten dolu bir veritabanıyla açılır.
#   5) backend + frontend image'larını build edip ayağa kaldırır.
#
# Kullanım:
#   ./setup.sh                              # GROQ_API_KEY'i sonra .env'e elle gir
#   ./setup.sh gsk_xxxxxxxxxxxxxxxxxxxx      # GROQ_API_KEY'i doğrudan ver
#   GROQ_API_KEY=gsk_xxx ./setup.sh          # ya da ortam değişkeniyle ver
#
# Gereksinimler: git, docker, docker compose (v2 plugin).

set -euo pipefail

BACKEND_REPO="https://github.com/palavaryunusemre/vocablearn-backend.git"
FRONTEND_REPO="https://github.com/palavaryunusemre/vocablearn-frontend.git"
INSTALL_DIR="${VOCABLEARN_INSTALL_DIR:-$PWD/vocablearn}"
GROQ_KEY_ARG="${1:-${GROQ_API_KEY:-}}"

log() { printf '\n\033[1;34m==> %s\033[0m\n' "$1"; }
die() { printf '\n\033[1;31mHATA: %s\033[0m\n' "$1" >&2; exit 1; }

command -v git >/dev/null 2>&1 || die "git bulunamadı. Önce git kurun."
command -v docker >/dev/null 2>&1 || die "docker bulunamadı. Önce Docker kurun."
docker compose version >/dev/null 2>&1 || die "'docker compose' (v2 plugin) bulunamadı."

log "Kurulum klasörü: $INSTALL_DIR"
mkdir -p "$INSTALL_DIR"
cd "$INSTALL_DIR"

if [ -d backend/.git ]; then
  log "backend zaten klonlanmış, güncelleniyor (git pull)"
  git -C backend pull --ff-only
else
  log "backend klonlanıyor"
  git clone "$BACKEND_REPO" backend
fi

if [ -d frontend/.git ]; then
  log "frontend zaten klonlanmış, güncelleniyor (git pull)"
  git -C frontend pull --ff-only
else
  log "frontend klonlanıyor"
  git clone "$FRONTEND_REPO" frontend
fi

cd backend

if [ ! -f .env ]; then
  log ".env dosyası oluşturuluyor (.env.example'dan)"
  cp .env.example .env
fi

if [ -n "$GROQ_KEY_ARG" ]; then
  log "GROQ_API_KEY .env dosyasına yazılıyor"
  if grep -q '^GROQ_API_KEY=' .env; then
    sed -i.bak "s|^GROQ_API_KEY=.*|GROQ_API_KEY=$GROQ_KEY_ARG|" .env && rm -f .env.bak
  else
    echo "GROQ_API_KEY=$GROQ_KEY_ARG" >> .env
  fi
else
  log "UYARI: GROQ_API_KEY verilmedi. Yapay zeka sohbet asistanı çalışmayacak."
  log "       Sonradan eklemek için: backend/.env içine GROQ_API_KEY=... yaz ve"
  log "       'docker compose up -d --build backend' çalıştır."
fi

log "Postgres ayağa kaldırılıyor"
docker compose up -d postgres

log "Postgres'in sağlıklı olması bekleniyor"
for i in $(seq 1 30); do
  status="$(docker inspect -f '{{.State.Health.Status}}' vocablearn-postgres 2>/dev/null || echo starting)"
  [ "$status" = "healthy" ] && break
  sleep 2
done
[ "$status" = "healthy" ] || die "Postgres 60sn içinde sağlıklı olmadı. 'docker logs vocablearn-postgres' kontrol edin."

DUMP_FILE="db-backup/vocablearn_backup.sql.gz"
TABLE_COUNT="$(docker exec vocablearn-postgres psql -U vocablearn -d vocablearn -tAc \
  "SELECT count(*) FROM information_schema.tables WHERE table_schema='public'" 2>/dev/null || echo 0)"

if [ "$TABLE_COUNT" -eq 0 ] 2>/dev/null && [ -f "$DUMP_FILE" ]; then
  log "Veritabanı boş -- yedek restore ediliyor ($DUMP_FILE)"
  gunzip -c "$DUMP_FILE" | docker exec -i vocablearn-postgres psql -U vocablearn -d vocablearn
else
  log "Veritabanında zaten $TABLE_COUNT tablo var veya yedek dosyası yok -- restore atlanıyor"
  log "(Backend ilk açılışta Flyway migration + seed verisiyle kendi şemasını zaten kurar.)"
fi

log "backend + frontend build edilip ayağa kaldırılıyor (ilk build birkaç dakika sürebilir)"
docker compose up -d --build backend frontend

log "Tamamlandı!"
echo "  Frontend : http://localhost:3000"
echo "  Backend  : http://localhost:8080"
echo "  Swagger  : http://localhost:8080/swagger-ui.html"
echo
echo "Durumu izlemek için: docker compose -f \"$INSTALL_DIR/backend/docker-compose.yml\" logs -f"
