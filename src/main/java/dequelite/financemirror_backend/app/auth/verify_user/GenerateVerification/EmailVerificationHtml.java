package dequelite.financemirror_backend.app.auth.verify_user.GenerateVerification;

import org.springframework.stereotype.Component;

@Component
public class EmailVerificationHtml {
    public static String html(String link, String code) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Verify Your Email | FinanceMirror</title>
                    <style>
                        body {
                            margin: 0;
                            padding: 0;
                            background-color: #f8f9fa;
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                            -webkit-font-smoothing: antialiased;
                        }

                        table {
                            border-spacing: 0;
                            width: 100%%;
                        }

                        td {
                            padding: 0;
                        }

                        .wrapper {
                            width: 100%%;
                            table-layout: fixed;
                            background-color: #f8f9fa;
                            padding-top: 40px;
                            padding-bottom: 40px;
                        }

                        .main-card {
                            width: 100%%;
                            max-width: 500px;
                            background-color: #ffffff;
                            margin: 0 auto;
                            border-radius: 16px;
                            border: 1px solid #eef0f2;
                            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
                        }

                        .content {
                            padding: 40px 30px;
                            text-align: center;
                        }

                        .logo-container {
                            margin-bottom: 20px;
                            display: inline-block;
                        }

                        .logo-circle {
                            width: 64px;
                            height: 64px;
                            border-radius: 50%%;
                            background-color: #0e3a2f;
                            background: radial-gradient(circle, #0e3a2f 35%%, #cca43b 36%%, #cca43b 45%%, #082a22 46%%);
                            margin: 0 auto;
                        }

                        h1 {
                            color: #0b1325;
                            font-size: 24px;
                            font-weight: 700;
                            margin: 0 0 4px 0;
                        }

                        .subtitle {
                            color: #64748b;
                            font-size: 13px;
                            margin: 0 0 32px 0;
                            font-weight: 500;
                        }

                        p {
                            color: #334155;
                            font-size: 15px;
                            line-height: 1.5;
                            margin: 0 0 24px 0;
                        }

                        .btn-link {
                            display: inline-block;
                            background-color: #0f172a;
                            color: #ffffff !important;
                            text-decoration: none;
                            padding: 14px 32px;
                            border-radius: 8px;
                            font-size: 15px;
                            font-weight: 600;
                            margin-bottom: 32px;
                        }

                        .code-container {
                            background-color: #f1f5f9;
                            border-radius: 8px;
                            padding: 16px;
                            margin-bottom: 32px;
                        }

                        .code-label {
                            font-size: 11px;
                            color: #64748b;
                            text-transform: uppercase;
                            letter-spacing: 1px;
                            margin-bottom: 8px;
                            font-weight: 600;
                        }

                        .code-value {
                            font-size: 28px;
                            font-weight: 700;
                            color: #0e3a2f;
                            letter-spacing: 4px;
                        }

                        .footer {
                            border-top: 1px solid #f1f5f9;
                            padding-top: 24px;
                            font-size: 12px;
                            color: #94a3b8;
                            text-align: center;
                            line-height: 1.5;
                        }
                    </style>
                </head>
                <body>

                    <center class="wrapper">
                        <table class="main-card" role="presentation">
                            <tr>
                                <td class="content">

                                    <div class="logo-container">
                                        <div class="logo-circle"></div>
                                    </div>

                                    <h1>Welcome to FinanceMirror</h1>
                                    <div class="subtitle">by Dequelite</div>

                                    <p>
                                        Thank you for signing up! To complete your registration
                                        and secure your account, please verify your email address
                                        by clicking the button below or using the verification code.
                                    </p>

                                    <a href="%s" target="_blank" class="btn-link">
                                        Verify Email Address
                                    </a>

                                    <div class="code-container">
                                        <div class="code-label">Your Verification Code</div>
                                        <div class="code-value">%s</div>
                                    </div>

                                    <p style="font-size: 13px; color: #64748b; margin-bottom: 0;">
                                        If you did not request this email, you can safely ignore it.
                                    </p>

                                    <table style="margin-top: 40px; width: 100%%;" role="presentation">
                                        <tr>
                                            <td class="footer">
                                                © 2026 FinanceMirror. All rights reserved.<br>
                                                You received this email because this address was used
                                                to create an account.
                                            </td>
                                        </tr>
                                    </table>

                                </td>
                            </tr>
                        </table>
                    </center>

                </body>
                </html>
                """.formatted(link, code);
    }
}