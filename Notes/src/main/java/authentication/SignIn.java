package authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * Servlet implementation class SignIn
 */
@WebServlet("/SignIn")
public class SignIn extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SignIn() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Set CORS headers for the response
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} finally {
			reader.close();
		}
		String payload = sb.toString();
		JSONObject data = new JSONObject(payload);
		String email = data.getString("email");
		String password = data.getString("password");
		System.out.println("Email " + email + ", password " + password);

		try {
			if (!exists(email)) {
				System.out.println("User does not exist");
				response.sendError(404, "User does not exists");
				return;
			}
			if (valid(email, password)) {
				System.out.println("User logged in successfully");
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.print("{\"status\": \"logged in successfully\", \"loggedIn\": \"true\"}");
			} else {
				System.out.println("Username or password incorrect");
				response.sendError(403, "Username or password incorrect");
				return;
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Set CORS headers for the response
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "DELETE");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		HttpSession session = request.getSession();
		session.removeAttribute("loggedIn");

		PrintWriter out = response.getWriter();
		out.print("logged out successfully");
	}

	public void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Access-Control-Allow-Methods", "*");
		res.setHeader("Access-Control-Allow-Headers", "Content-Type");
		res.setHeader("Access-Control-Max-Age", "86400");
		res.setHeader("Access-Control-Allow-Credentials", "true");
	}

	public boolean exists(String email) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "");
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_info WHERE email=(?)");
		stmt.setString(1, email);
		ResultSet rst = stmt.executeQuery();
		System.out.print("Executed");
		return rst.next();
	}

	public boolean valid(String email, String password) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "");
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_info WHERE email=(?) AND password=(?)");
		stmt.setString(1, email);
		stmt.setString(2, password);
		ResultSet rst = stmt.executeQuery();
		System.out.print("Executed");
		return rst.next();
	}
}
