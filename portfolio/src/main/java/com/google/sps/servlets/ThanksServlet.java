import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/thanks")
public class ThanksServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().println("谢谢你的支持！");
        response.getWriter().println("<a href=\"./index.html\">请点击这里跳转回主页！</a>");
    }
}