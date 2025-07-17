#!/bin/bash

APP_DIR="/home/ubuntu/app"
NGINX_CONF="$APP_DIR/nginx/app.conf"

CURRENT_PORT=$(grep -oP 'server 127.0.0.1:\K\d+' "$NGINX_CONF")
NEXT_PORT=8082
[ "$CURRENT_PORT" == "8082" ] && NEXT_PORT=8081

echo "🔁 현재 포트: $CURRENT_PORT → 새 포트: $NEXT_PORT"

docker-compose -f "$APP_DIR/docker-compose.${NEXT_PORT}.yml" up -d --build

# 헬스 체크
until curl -s "http://localhost:${NEXT_PORT}/actuator/health" | grep '"status":"UP"' > /dev/null; do
  echo "⏳ 헬스체크 대기중..."
  sleep 2
done

# nginx 설정 변경 및 reload
sed -i "s/127.0.0.1:${CURRENT_PORT}/127.0.0.1:${NEXT_PORT}/" "$NGINX_CONF"
sudo systemctl reload nginx

docker-compose -f "$APP_DIR/docker-compose.${CURRENT_PORT}.yml" down

echo "✅ 배포 완료 (새 포트: $NEXT_PORT)"