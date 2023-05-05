package note;

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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class Note
 */
@WebServlet("/Note")
public class Note extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Note() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Set CORS headers for the response
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		if (request.getParameter("loggedIn") == null) {
			response.sendError(502, "not logged in");
			return;
		}
		String email = request.getParameter("email");
		try {
			sendRes(response, email);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		if (request.getParameter("loggedIn") == null) {
			response.sendError(502, "not logged in");
			return;
		}

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
		String title = data.getString("title");
		String description = data.getString("description");
		try {
			if (exists(email)) {
				if (addNote(email, title, description)) {
					response.setContentType("application/json");
					sendRes(response, email);
				} else {
					System.out.println("Could not add note");
					response.sendError(501, "Could not add note");
					return;
				}
			} else {
				System.out.println("User does not exist");
				response.sendError(404, "User does not exists");
				return;
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Set CORS headers for the response
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "DELETE");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		if (request.getParameter("loggedIn") == null) {
			response.sendError(502, "not logged in");
			return;
		}
		String email = request.getParameter("email");
		String title = request.getParameter("title");
		try {
			if (exists(email)) {
				if (removeNote(email, title)) {
					response.setContentType("application/json");
					sendRes(response, email);
				} else {
					System.out.println("Could not remove note");
					response.sendError(501, "Could not remove note");
					return;
				}
			} else {
				System.out.println("User does not exist");
				response.sendError(404, "User does not exists");
				return;
			}
		} catch (ClassNotFoundException | SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
		res.setHeader("Access-Control-Allow-Headers", "Content-Type");
		res.setHeader("Access-Control-Max-Age", "86400");
		res.setHeader("Access-Control-Allow-Credentials", "true");
	}

	protected boolean addNote(String email, String title, String description)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "");
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO user_notes VALUES (?, ?, ?)");
		stmt.setString(1, email);
		stmt.setString(2, title);
		stmt.setString(3, description);
		System.out.println("Note added successfully");
		Integer res = stmt.executeUpdate();
		System.out.println("Executed? " + res);
		return res == 1;
	}
	
	protected boolean removeNote(String email, String title)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "");
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM user_notes WHERE title = ? AND email = ?");
		stmt.setString(1, title);
		stmt.setString(2, email);
		System.out.println("Note removed successfully");
		Integer res = stmt.executeUpdate();
		System.out.println("Executed? " + res);
		return res == 1;
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

	public ResultSet getNotes(String email) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "");
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_notes WHERE email=(?)");
		stmt.setString(1, email);
		ResultSet rst = stmt.executeQuery();
		return rst;
	}

	protected void sendRes(HttpServletResponse response, String email)
			throws ClassNotFoundException, SQLException, IOException {
		ResultSet notes = getNotes(email);
		JSONArray dataArray = new JSONArray();
		while (notes.next()) {
			JSONObject obj = new JSONObject();
			obj.put("email", notes.getString("email"));
			obj.put("title", notes.getString("title"));
			obj.put("description", notes.getString("description"));
			dataArray.put(obj);
		}
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(dataArray.toString());
	}

}
