<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>webblog</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Segoe UI', 'Apple SD Gothic Neo', sans-serif;
            background-color: #f2ede8;
            color: #4a4a4a;
            font-size: 14px;
            line-height: 1.6;
        }

        a { color: #6e9bbf; text-decoration: none; }
        a:hover { color: #4a7fa0; text-decoration: underline; }

        .wb-wrap {
            max-width: 1100px;
            margin: 0 auto;
            padding: 0 16px 30px;
        }

        /* 헤더 */
        .wb-header {
            background: linear-gradient(135deg, #ddeaf5 0%, #e8f0f8 100%);
            border-bottom: 2px solid #bdd4e8;
            padding: 16px 24px;
            border-radius: 0 0 12px 12px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .wb-header h1 {
            font-size: 21px;
            font-weight: 700;
            color: #3a6080;
            letter-spacing: -0.5px;
        }

        .wb-header h1 a { color: #3a6080; }

        .member-badge {
            background-color: #b8d4e8;
            color: #3a5070;
            font-size: 12px;
            padding: 4px 14px;
            border-radius: 20px;
            font-weight: 500;
        }

        hr {
            border: none;
            border-top: 1px solid #ddd5cc;
            margin: 14px 0;
        }

        /* 레이아웃 */
        .wb-row {
            display: flex;
            flex-wrap: wrap;
            gap: 18px;
            align-items: flex-start;
        }

        .wb-menu { flex: 1 1 170px; min-width: 155px; }
        .wb-body { flex: 3 1 400px; min-width: 0; }

        @media (max-width: 720px) {
            .wb-row { flex-direction: column; }
        }

        /* 사이드 메뉴 */
        .wb-menu ul {
            list-style: none;
            background: #fff;
            border: 1px solid #ddd5cc;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
        }

        .wb-menu ul li {
            border-bottom: 1px solid #f0ebe5;
        }

        .wb-menu ul li:last-child { border-bottom: none; }

        .wb-menu ul li a,
        .wb-menu ul li span {
            display: block;
            padding: 11px 16px;
            color: #5a7a95;
            font-size: 13px;
            transition: background 0.15s;
        }

        .wb-menu ul li span {
            color: #8a9aaa;
            font-weight: 500;
            background-color: #f5f1ee;
            cursor: default;
        }

        .wb-menu ul li a:hover {
            background-color: #eaf3fb;
            color: #3a6080;
            text-decoration: none;
        }

        /* 본문 */
        .wb-body-inner {
            background: #fff;
            border: 1px solid #e5ddd6;
            border-radius: 10px;
            padding: 26px 30px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.04);
        }

        h2 {
            font-size: 17px;
            color: #3a6080;
            margin-bottom: 16px;
            padding-bottom: 8px;
            border-bottom: 2px solid #d9e8f5;
        }

        h3 {
            font-size: 15px;
            color: #5a7080;
            margin-bottom: 10px;
            margin-top: 16px;
        }

        /* 테이블 */
        table {
            width: 100%;
            border-collapse: collapse;
        }

        table th {
            background-color: #ddeaf5;
            color: #3a6080;
            font-weight: 600;
            padding: 10px 12px;
            text-align: left;
            font-size: 13px;
            border: 1px solid #c8dcec;
        }

        table td {
            padding: 9px 12px;
            border: 1px solid #e0d8d0;
            color: #555;
            font-size: 13px;
            vertical-align: top;
            word-break: break-word;
        }

        tr:hover td { background-color: #f8f3ef; }

        /* 폼 */
        input[type="text"],
        input[type="email"],
        input[type="password"],
        input[type="number"],
        textarea,
        select {
            padding: 7px 11px;
            border: 1px solid #cdd9e5;
            border-radius: 6px;
            background-color: #fafcff;
            color: #4a4a4a;
            font-size: 13px;
            transition: border-color 0.2s;
        }

        input[type="text"]:focus,
        input[type="email"]:focus,
        input[type="password"]:focus,
        input[type="number"]:focus,
        textarea:focus,
        select:focus {
            outline: none;
            border-color: #8ab4d4;
            background-color: #fff;
        }

        input[type="checkbox"] { accent-color: #7a9cbf; }

        /* 버튼 */
        button, input[type="submit"], .btn {
            background-color: #8ab4d4;
            color: #fff;
            border: none;
            padding: 7px 16px;
            border-radius: 6px;
            font-size: 13px;
            cursor: pointer;
            transition: background 0.2s;
            margin-top: 4px;
        }

        button:hover, input[type="submit"]:hover, .btn:hover {
            background-color: #6a99bc;
        }

        /* 푸터 */
        .wb-footer {
            background-color: #e8dfd8;
            border-top: 1px solid #d5ccc4;
            border-radius: 10px 10px 0 0;
            text-align: center;
            padding: 14px;
            color: #999;
            font-size: 12px;
            margin-top: 4px;
        }

        /* 알림 */
        .alert { padding: 11px 15px; border-radius: 8px; margin-bottom: 14px; font-size: 13px; }
        .alert-info    { background-color: #ddeaf5; color: #3a6080; border: 1px solid #b8d4e8; }
        .alert-warning { background-color: #fdf3dc; color: #8a6a20; border: 1px solid #f0d898; }
        .alert-danger  { background-color: #fde8e8; color: #904040; border: 1px solid #f0b8b8; }

        /* 블로그 카드 */
        .blog-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
            gap: 16px;
            margin-top: 14px;
        }

        .blog-card {
            background: #f8f4f0;
            border: 1px solid #e0d8d0;
            border-radius: 10px;
            padding: 18px 20px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.04);
            transition: box-shadow 0.2s, transform 0.15s;
        }

        .blog-card:hover {
            box-shadow: 0 4px 14px rgba(0,0,0,0.09);
            transform: translateY(-2px);
        }

        .blog-card h3 { font-size: 14px; color: #3a6080; margin-bottom: 6px; margin-top: 0; }
        .blog-card p  { font-size: 12px; color: #999; }

        /* 로그인/승인대기 박스 */
        .center-box {
            max-width: 420px;
            margin: 40px auto;
            background: #fff;
            border: 1px solid #e0d8d0;
            border-radius: 12px;
            padding: 36px 40px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.06);
            text-align: center;
        }

        .center-box h2 { text-align: center; }
        .center-box p { color: #777; line-height: 1.8; margin-bottom: 10px; font-size: 13px; }

        .kakao-btn {
            display: block;
            width: 100%;
            background-color: #fee500;
            color: #3c1e1e !important;
            text-align: center;
            padding: 10px;
            border-radius: 6px;
            font-weight: 600;
            margin-top: 8px;
            font-size: 13px;
        }

        .kakao-btn:hover {
            background-color: #e6cf00;
            text-decoration: none !important;
        }

        .text-muted { color: #aaa; font-size: 12px; }
        .mt-10 { margin-top: 10px; }
        .mt-20 { margin-top: 20px; }
        .mb-10 { margin-bottom: 10px; }
        .mb-20 { margin-bottom: 20px; }
    </style>
</head>
<body>
<div class="wb-wrap">
    <div class="wb-header">
        <tiles:insertAttribute name="header"/>
    </div>
    <hr/>
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
    <hr/>
    <div class="wb-footer">
        <tiles:insertAttribute name="footer"/>
    </div>
</div>
</body>
</html>