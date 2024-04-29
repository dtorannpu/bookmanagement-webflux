# 書籍管理APIリポジトリ

## 実行手順

* DBコンテナ起動
```
docker compose up
```

* DBマイグレーション
```
./gradlew flywayMigrate
```

* jOOQコード生成
```
./gradlew jooqCodegen
```

* OpenAPIコード生成
```
./gradlew openApiGenerate
```

* Spring Boot起動
```
./gradlew bootRun
```

## OpenAPIページ
Spring Bootアプリケーションを起動して以下のリンクを表示  
http://localhost:8080/swagger-ui.html
