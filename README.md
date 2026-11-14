# VocabLearn — Backend

YDS/YÖKDİL odaklı İngilizce öğrenme platformunun Spring Boot backend'i.
Seviyeli okuma parçaları, bağlaç (adverbial clause) ve modal konu anlatımları,
bunlara bağlı quiz/alıştırma motoru, ilerleme takibi ve yapay zekâ destekli
soru-cevap asistanı sunar.

İlgili frontend: [vocablearn-frontend](https://github.com/palavaryunusemre/vocablearn-frontend)

---

## İçindekiler

- [Teknoloji Yığını](#teknoloji-yığını)
- [Docker ile Çalıştırma (önerilen)](#docker-ile-çalıştırma-önerilen)
- [Lokal (Docker'sız) Çalıştırma](#lokal-dockersız-çalıştırma)
- [Ortam Değişkenleri / Gizli Anahtarlar](#ortam-değişkenleri--gizli-anahtarlar)
- [Veritabanı Yedeği](#veritabanı-yedeği)
- [Sıfırdan Kurulum Scripti](#sıfırdan-kurulum-scripti)
- [Roller](#roller)
- [REST API](#rest-api)

---

## Teknoloji Yığını

| Katman | Teknoloji |
|---|---|
| Framework | Spring Boot `4.1.0` (Java `17`) |
| Veritabanı | PostgreSQL (Spring Data JPA / Hibernate), şema geçişleri Flyway (`V1`–`V7`) |
| Kimlik doğrulama | Spring Security + JWT (`jjwt` `0.12.6`), HttpOnly çerez tabanlı stateless oturum |
| Yapay zekâ | Spring AI, Groq'un OpenAI-uyumlu uç noktası (`/api/chat`) |
| Alıştırma motoru | Tip bazlı Generator/Grader factory deseni (`MULTIPLE_CHOICE`, `FILL_BLANK`, `MATCHING`, `TYPING`) |
| API dokümantasyonu | springdoc-openapi (`/swagger-ui.html`) |

## Docker ile Çalıştırma (önerilen)

Bu repo ile `../frontend` (yani [vocablearn-frontend](https://github.com/palavaryunusemre/vocablearn-frontend))
**yan yana klasörler** olarak klonlanmış olmalı:

```
vocablearn/
├── backend/   ← bu repo
└── frontend/
```

```bash
cd backend
cp .env.example .env   # .env içine gerçek GROQ_API_KEY'i yaz
docker compose up -d --build
```

Bu tek komut üç servisi ayağa kaldırır:

| Servis | Adres | Açıklama |
|---|---|---|
| `postgres` | `localhost:5433` | Veritabanı (named volume ile kalıcı) |
| `backend` | `localhost:8080` | Spring Boot API, Swagger: `/swagger-ui.html` |
| `frontend` | `localhost:3000` | Next.js arayüz |

İlk build birkaç dakika sürer (Maven bağımlılıkları + Next.js build). Sonraki
`docker compose up -d --build` çağrıları Docker layer cache sayesinde çok
daha hızlıdır.

## Lokal (Docker'sız) Çalıştırma

```bash
docker compose up -d postgres   # sadece veritabanı
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

`local` profili, gitignore'lu `src/main/resources/application-local.yaml`
dosyasından `spring.ai.openai.api-key` değerini okur (bkz. aşağıdaki
Ortam Değişkenleri bölümü).

## Ortam Değişkenleri / Gizli Anahtarlar

**Evet, anahtarlar env/profil dosyalarında tutuluyor, koda gömülü değil:**

| Senaryo | Dosya | Git'e gider mi? |
|---|---|---|
| Docker Compose | `.env` (kök dizinde, `.env.example`'dan kopyalanır) | ❌ Hayır (`.gitignore`) |
| Lokal `./mvnw` + `local` profili | `src/main/resources/application-local.yaml` | ❌ Hayır (`.gitignore`) |
| Şablon/örnek | `.env.example` | ✅ Evet (gerçek değer içermez) |

Gereken anahtarlar:

| Değişken | Amaç |
|---|---|
| `GROQ_API_KEY` | `/api/chat` yapay zekâ asistanı için Groq (OpenAI-uyumlu) API anahtarı |
| `JWT_SECRET` | JWT imzalama anahtarı, en az 32 byte rastgele değer (`openssl rand -base64 48` ile üretilir). [setup.sh](scripts/setup.sh) boşsa otomatik üretir; `application.yaml`'daki `change-this-...` değeri yalnızca hiçbir yerden anahtar verilmediğinde devreye giren, üretimde **kullanılmaması gereken** bir geliştirme varsayılanıdır. |

## Veritabanı Yedeği

Parça/bağlaç/modal/quiz içeriği zaten `src/main/resources/data/*.json`
dosyalarından **otomatik seed edilir** (ilk açılışta) -- bunun için ayrı bir
DB yedeğine gerek yok, fresh bir kurulum bu içerikle kendiliğinden dolu gelir.

`db-backup/` klasörü yalnızca **gerçek kullanıcı hesaplarını/ilerlemeyi**
bir makineden diğerine taşımak istediğinizde kullanılır ve bilerek
`.gitignore`'dadır -- şifre hash'leri içerdiği için **public repoya asla
commit edilmemelidir**. Taşımak isterseniz dosyayı elden/özel bir kanaldan
(USB, `scp`, vb.) hedef makineye kopyalayın.

Yedek almak için:
```bash
mkdir -p db-backup
docker exec vocablearn-postgres pg_dump -U vocablearn -d vocablearn --no-owner --no-privileges \
  | gzip > db-backup/vocablearn_backup.sql.gz
```

Restore etmek için (hedef veritabanı boşken):
```bash
gunzip -c db-backup/vocablearn_backup.sql.gz | docker exec -i vocablearn-postgres psql -U vocablearn -d vocablearn
```

## Sıfırdan Kurulum Scripti

Yeni/boş bir makinede (sadece `git`, `docker` ve `docker compose` kurulu
olması yeterli) backend + frontend'i tek komutla ayağa kaldırır. İçerik
(parçalar/bağlaçlar/modallar/quiz'ler) seeder'lar sayesinde otomatik gelir;
eğer elden taşıdığınız bir `db-backup/vocablearn_backup.sql.gz` varsa script
onu da restore eder:

```bash
curl -fsSL https://raw.githubusercontent.com/palavaryunusemre/vocablearn-backend/main/scripts/setup.sh -o setup.sh
chmod +x setup.sh
./setup.sh <GROQ_API_KEY>      # GROQ_API_KEY opsiyonel argüman
```

Script, `vocablearn-backend` ve `vocablearn-frontend` repolarını `./vocablearn/`
altına klonlar, `.env` dosyasını hazırlar, Postgres'i ayağa kaldırıp veritabanı
boşsa yedeği restore eder, ardından backend + frontend image'larını build edip
başlatır. Detaylar için [scripts/setup.sh](scripts/setup.sh).

## Roller

| Rol | Kapsam |
|---|---|
| `USER` | Parçaları okur, alıştırma/quiz çözer, ilerlemesini görür, chat asistanına soru sorar |
| `ADMIN` | `/api/admin/**` altındaki yönetimsel uçlara erişir |

## REST API

| Alan | Base path | Not |
|---|---|---|
| Auth | `/api/auth` | `register`, `login`, `change-password`, `logout` |
| Parçalar | `/api/passages` | Seviyeye göre filtrelenebilir liste + detay |
| Parça soruları | `/api/passages/{id}/questions` | Okuduğunu anlama soruları |
| Parça alıştırmaları | `/api/passages/{id}/exercises/{type}` | `MULTIPLE_CHOICE` / `FILL_BLANK` / `MATCHING` / `TYPING` |
| Bağlaçlar | `/api/conjunctions`, `/api/conjunctions/{id}/exercises/{type}`, `/api/conjunctions/category-quiz` | |
| Modallar | `/api/modals`, `/api/modals/category-quiz`, `/api/modals/translation-quiz` | |
| Quiz setleri | `/api/quizzes` | Bağımsız sınav setleri |
| İlerleme | `/api/progress/me` | Kelime/soru istatistikleri |
| Chat | `/api/chat` | Yapay zekâ dilbilgisi asistanı (kimlik doğrulama gerektirir) |

Tüm uçların tam listesi için backend ayaktayken `/swagger-ui.html`.
