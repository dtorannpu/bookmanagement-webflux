import "@typespec/http";
import "@typespec/openapi";
import "@typespec/openapi3";

using Http;
using OpenAPI;

/**
 * 書籍を管理するAPI
 */
@service(#{ title: "書籍管理API" })
@info(#{ version: "1.0.0", contact: #{ name: "Ryoichi Hayashi" } })
namespace API;

/**
 * 著者作成リクエスト
 */
@extension(
  "x-stoplight",
  #{
    id: "f56e044dc35c9",
  }
)
@summary("著者作成リクエスト")
model CreateAuthorRequest {
  /**
   * 著者名
   */
  @minLength(1) @maxLength(100) name: string;

  /**
   * 誕生日
   */
  birthday?: plainDate;
}

/**
 * 著者更新リクエスト
 */
@extension(
  "x-stoplight",
  #{
    id: "a5e173dacc7a0",
  }
)
@summary("著者更新リクエスト")
model UpdateAuthorRequest {
  /**
   * ID
   */
  id: integer;

  /**
   * 著者名
   */
  @minLength(1) @maxLength(100) name: string;

  /**
   * 誕生日
   */
  birthday?: plainDate;
}

/**
 * 書籍作成リクエスト
 */
@extension(
  "x-stoplight",
  #{
    id: "28493631cd667",
  }
)
@summary("書籍作成リクエスト")
model CreateBookRequest {
  /**
   * ISBN
   */
  @maxLength(17) isbn?: string;

  /**
   * 著者ID
   */
  authorId: integer;

  /**
   * タイトル
   */
  @minLength(1) @maxLength(100) title: string;
}

/**
 * 書籍更新リクエスト
 */
@extension(
  "x-stoplight",
  #{
    id: "83c501596f526",
  }
)
@summary("書籍更新リクエスト")
model UpdateBookRequest {
  /**
   * ID
   */
  id: integer;

  /**
   * ISBN
   */
  @maxLength(17) isbn?: string;

  /**
   * 著者ID
   */
  authorId: integer;

  /**
   * タイトル
   */
  @minLength(1) @maxLength(100) title: string;
}

/**
 * 著者
 */
@extension(
  "x-stoplight",
  #{
    id: "g8akxct1grn56",
  }
)
@summary("著者")
model Author {
  /**
   * ID
   */
  id: integer;

  /**
   * 著者名
   */
  name: string;

  /**
   * 誕生日
   */
  birthday?: plainDate;

  /**
   * 著者の書籍
   */
  books: AuthorBook[];
}

/**
 * 著者の書籍
 */
@extension(
  "x-stoplight",
  #{
    id: "2g0opobb408k1",
  }
)
@summary("著者の書籍")
model AuthorBook {
  /**
   * ID
   */
  id: integer = 1;

  /**
   * ISBN
   */
  isbn?: string;

  /**
   * タイトル
   */
  title: string;
}

/**
 * 書籍
 */
@extension(
  "x-stoplight",
  #{
    id: "1k3jugezay1ke",
  }
)
@summary("書籍")
model Book {
  /**
   * ID
   */
  id: integer;

  /**
   * ISBN
   */
  isbn?: string;

  /**
   * タイトル
   */
  title: string;

  author: BookAuthor;
}

/**
 * 書籍の著者
 */
@extension(
  "x-stoplight",
  #{
    id: "taweepph9g19d",
  }
)
@summary("書籍の著者")
model BookAuthor {
  /**
   * ID
   */
  authorId: integer;

  /**
   * 著者名
   */
  name: string;

  /**
   * 誕生日
   */
  birthday?: plainDate;
}

/**
 * 書籍登録に成功した場合のレスポンス
 */
@extension(
  "x-stoplight",
  #{
    id: "qi3wrilss487o",
  }
)
@summary("CreateBookResponse")
model CreateBookResponse {
  /**
   * ID
   */
  id: integer;
}

/**
 * 書籍更新に成功した場合のレスポンス
 */
@extension(
  "x-stoplight",
  #{
    id: "2md7c6pz5k1et",
  }
)
@summary("UpdateBookResponse")
model UpdateBookResponse {
  /**
   * ID
   */
  id: integer;
}

/**
 * 著者登録に成功した場合のレスポンス
 */
@extension(
  "x-stoplight",
  #{
    id: "8cc07plgas186",
  }
)
@summary("CreateAuthorResponse")
model CreateAuthorResponse {
  /**
   * ID
   */
  id: integer;
}

/**
 * 著者登録に成功した場合のレスポンス
 */
@extension(
  "x-stoplight",
  #{
    id: "dq90t3syc4b9x",
  }
)
@summary("UpdateAuthorResponse")
model UpdateAuthorResponse {
  /**
   * ID
   */
  id: integer;
}

/**
 * 著者更新
 */
@tag("author")
@route("/authors")
@patch(#{implicitOptionality: false})
@summary("著者更新")
op updateAuthor(@body body: UpdateAuthorRequest): UpdateAuthorResponse;

/**
 * 著者登録
 */
@tag("author")
@route("/authors")
@post
@summary("著者登録")
op createAuthor(@body body: CreateAuthorRequest): CreateAuthorResponse;

/**
 * 書籍の検索
 */
@tag("book")
@route("/books")
@get
@summary("書籍の検索")
op searchBook(
  /**
   * 書籍タイトル ※部分一致検索
   */
  @query(#{ explode: true }) bookTitle?: string,

  /**
   * 著者名 ※部分一致検索
   */
  @query(#{ explode: true }) authorName?: string,

  /**
   * ISBN ※完全一致検索
   */
  @query(#{ explode: true }) isbn?: string,
): Body<Book[]>;

/**
 * 書籍更新
 */
@tag("book")
@route("/books")
@patch(#{implicitOptionality: false})
@summary("書籍更新")
op updateBook(@body body: UpdateBookRequest): UpdateBookResponse;

/**
 * 書籍登録
 */
@tag("book")
@route("/books")
@post
@summary("書籍登録")
op createBook(@body body: CreateBookRequest): CreateBookResponse;

/**
 * 著者に紐づく本を取得できる機能
 */
@tag("author")
@route("/authors/{id}")
@get
@summary("著者に紐づく本を取得できる機能")
op getAuthorById(
  /**
   * ID
   */
  @path id: integer,
): Author | NotFoundResponse;
