import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/anonymous")
public class AnonymousServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().println("<p>评论为空或请填写带‘*’号的必填信息，评论失败<br></br></p>");
        response.getWriter().println("<a href=\"./index.html\">请点击这里跳转回主页！</a>");
    }
}