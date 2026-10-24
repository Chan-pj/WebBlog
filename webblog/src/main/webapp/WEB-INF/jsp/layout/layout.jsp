<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>webblog</title>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }

        :root {
            --blue:    #6b9fd4;
            --blue-dk: #4d84bc;
            --blue-lt: #eaf4fb;
            --bg:      #f7f4f1;
            --card:    #ffffff;
            --border:  #e8e2dc;
            --text:    #2e2e2e;
            --muted:   #9a9a9a;
            --r:       8px;
        }

        body {
            font-family: 'Noto Sans KR', sans-serif;
            background: var(--bg);
            color: var(--text);
            font-size: 14px;
            line-height: 1.7;
        }

        a { color: var(--blue-dk); text-decoration: none; }
        a:hover { color: var(--blue); text-decoration: underline; }

        .wb-wrap { max-width: 1080px; margin: 0 auto; padding: 0 20px 60px; }

        /* 헤더 */
        .wb-header {
            padding: 20px 0 16px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            border-bottom: 1.5px solid var(--border);
            margin-bottom: 28px;
        }

        .wb-header h1 { font-size: 18px; font-weight: 500; letter-spacing: -0.3px; }
        .wb-header h1 a { color: var(--text); }

        .member-badge {
            font-size: 12px;
            color: var(--muted);
        }

        hr { display: none; }

        /* 레이아웃 */
        .wb-row { display: flex; gap: 32px; align-items: flex-start; }
        .wb-menu { flex: 0 0 160px; }
        .wb-body { flex: 1 1 0; min-width: 0; }

        @media (max-width: 640px) {
            .wb-row { flex-direction: column; }
            .wb-menu { flex: none; width: 100%; }
        }

        /* 사이드 메뉴 */
        .wb-menu ul { list-style: none; }
        .wb-menu ul li { border-bottom: 1px solid var(--border); }
        .wb-menu ul li:last-child { border-bottom: none; }

        .wb-menu ul li a,
        .wb-menu ul li span {
            display: block;
            padding: 9px 4px;
            font-size: 13px;
            color: var(--muted);
            transition: color 0.15s;
        }

        .wb-menu ul li span {
            font-size: 11px;
            font-weight: 500;
            letter-spacing: 0.8px;
            text-transform: uppercase;
            color: #c0b8b0;
            cursor: default;
        }

        .wb-menu ul li a:hover { color: var(--text); text-decoration: none; }

        /* 본문 */
        .wb-body-inner {
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: var(--r);
            padding: 32px 36px;
        }

        h2 {
            font-size: 16px;
            font-weight: 500;
            color: var(--text);
            margin-bottom: 20px;
            padding-bottom: 12px;
            border-bottom: 1px solid var(--border);
        }

        h3 {
            font-size: 14px;
            font-weight: 500;
            color: var(--text);
            margin: 20px 0 10px;
        }

        /* 테이블 */
        table { width: 100%; border-collapse: collapse; font-size: 13px; }

        table th {
            padding: 10px 14px;
            text-align: left;
            font-size: 11px;
            font-weight: 500;
            letter-spacing: 0.6px;
            text-transform: uppercase;
            color: var(--muted);
            border-bottom: 1.5px solid var(--border);
        }

        table td {
            padding: 12px 14px;
            border-bottom: 1px solid var(--border);
            color: var(--text);
            vertical-align: top;
        }

        tr:last-child td { border-bottom: none; }
        tr:hover td { background: var(--blue-lt); }

        /* 버튼 */
        button, input[type="submit"], .btn {
            display: inline-block;
            background: var(--text);
            color: #fff;
            border: none;
            padding: 8px 16px;
            border-radius: var(--r);
            font-size: 13px;
            font-weight: 500;
            cursor: pointer;
            font-family: inherit;
            transition: opacity 0.15s;
            letter-spacing: -0.2px;
        }

        button:hover, input[type="submit"]:hover, .btn:hover { opacity: 0.75; }

        .btn-primary { background: var(--blue); }
        .btn-danger  { background: #e0a0a0; }
        .btn-ghost {
            background: transparent;
            color: var(--muted);
            border: 1px solid var(--border);
        }
        .btn-ghost:hover { opacity: 1; color: var(--text); border-color: #bbb; }
        .btn-sm { padding: 5px 12px; font-size: 12px; }

        /* 폼 */
        input[type="text"],
        input[type="email"],
        input[type="password"],
        input[type="number"],
        textarea,
        select {
            width: 100%;
            padding: 9px 12px;
            border: 1px solid var(--border);
            border-radius: var(--r);
            background: var(--bg);
            color: var(--text);
            font-size: 13px;
            font-family: inherit;
            transition: border-color 0.15s;
        }

        input:focus, textarea:focus, select:focus {
            outline: none;
            border-color: var(--blue);
            background: #fff;
        }

        input[type="checkbox"] { accent-color: var(--blue); }

        label {
            display: block;
            font-size: 12px;
            color: var(--muted);
            margin-bottom: 5px;
        }

        /* 알림 */
        .alert {
            padding: 11px 14px;
            border-radius: var(--r);
            margin-bottom: 16px;
            font-size: 13px;
            background: var(--blue-lt);
            color: var(--blue-dk);
            border: 1px solid #c8dff0;
        }

        /* 카카오 버튼 */
        .kakao-btn {
            display: block;
            width: 100%;
            background: #fee500;
            color: #3c1e1e;
            text-align: center;
            padding: 10px;
            border-radius: var(--r);
            font-weight: 500;
            font-size: 13px;
            margin-top: 8px;
            border: none;
            cursor: pointer;
            transition: opacity 0.15s;
        }

        .kakao-btn:hover { opacity: 0.85; text-decoration: none; color: #3c1e1e; }

        /* 블로그 카드 */
        .blog-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 14px;
            margin-top: 16px;
        }

        .blog-card {
            background: var(--bg);
            border: 1px solid var(--border);
            border-radius: var(--r);
            padding: 18px 20px;
            transition: border-color 0.15s;
        }

        .blog-card:hover { border-color: var(--blue); }
        .blog-card h3 { font-size: 14px; margin: 0 0 6px; }
        .blog-card p  { font-size: 12px; color: var(--muted); }

        /* 센터 박스 (로그인/대기) */
        .center-box {
            max-width: 380px;
            margin: 40px auto;
        }

        .center-box h2 { border: none; padding: 0; margin-bottom: 24px; }
        .center-box p  { color: var(--muted); font-size: 13px; margin-bottom: 10px; }

        /* 푸터 */
        .wb-footer {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid var(--border);
            text-align: center;
            font-size: 12px;
            color: var(--muted);
        }

        /* 유틸 */
        .text-muted { color: var(--muted); font-size: 12px; }
        .mt-8  { margin-top: 8px; }
        .mt-16 { margin-top: 16px; }
        .mt-24 { margin-top: 24px; }
        .mb-8  { margin-bottom: 8px; }
        .mb-16 { margin-bottom: 16px; }
        .flex  { display: flex; align-items: center; gap: 8px; }
        .gap-4 { gap: 4px; }
        .between { justify-content: space-between; }
        .wb-paging { margin: 16px 0; text-align: center; font-size: 13px; }
        .wb-paging a, .wb-paging strong { display: inline-block; min-width: 24px; padding: 2px 4px; }
        .wb-paging img { vertical-align: middle; }
    </style>
    <script>
        // 목록 페이지 이동: 현재 URL 의 다른 파라미터(검색어, 다른 목록의 페이지 등)는 유지하고 페이지 번호만 바꾼다.
        function wbMovePage(param, pageNo) {
            var url = new URL(window.location.href);
            url.searchParams.set(param, pageNo);
            window.location.href = url.toString();
        }
        function goPage(pageNo)     { wbMovePage('pageIndex', pageNo); }
        function goBlogPage(pageNo) { wbMovePage('blogPage', pageNo); }
        function goUserPage(pageNo) { wbMovePage('userPage', pageNo); }
    </script>
</head>
<body>
<div class="wb-wrap">
    <div class="wb-header">
        <tiles:insertAttribute name="header"/>
    </div>
    <div class="wb-row">
        <div class="wb-menu">
            <tiles:insertAttribute name="menu"/>
        </div>
        <div class="wb-body">
            <div class="wb-body-inner">
                <tiles:insertAttribute name="body"/>
            </div>
        </div>
    </div>
    <div class="wb-footer">
        <tiles:insertAttribute name="footer"/>
    </div>
</div>
</body>
</html>