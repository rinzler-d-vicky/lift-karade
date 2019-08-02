package app;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

class Greeting {

	private final long id;
	private final String content;

	public Greeting(long id, String content) {
		this.id = id;
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}
}

@RestController
public class ApiController {
    private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@RequestMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

	@RequestMapping("/users/list")
	public ArrayList<User> listUsers() {
		try {
			return User.List();
		} catch (SQLException e) {
			return new ArrayList<User>();
		}
	}

	@RequestMapping("/user/view")
	public User viewUser(
		@RequestParam(value = "id", defaultValue = "0") String idString
	){
		try {
			int id = Integer.parseInt(idString);
			User user;
			user = new User(id);
			return user;
		} catch (SQLException e) {
			return new User(0, "N/A", "N/A", new ArrayList<Integer>());
		}
	}

	@RequestMapping("/user/delete")
	public boolean deleteUser(
		@RequestParam(value = "id", defaultValue = "0") String idString
	){
		try {
			int id = Integer.parseInt(idString);
			User.Delete(id);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	@RequestMapping("/user/add")
	public User addUser(
		@RequestParam(value = "name") String name,
		@RequestParam(value = "card") String card,
		@RequestParam(value = "floors") String floorsCSV
	) {
		try {
			ArrayList<Integer> floors = new ArrayList<Integer>();
			String[] floorsString = floorsCSV.split(",");
			for (int i = 0; i < floorsString.length; i++) {
				floors.add(Integer.parseInt(floorsString[i]));
			}

			User user;
			user = new User(name, card, floors);
			user.save();
			return user;
		} catch (Exception e) {
			return new User(0, "N/A", "N/A", new ArrayList<Integer>());
		}
	}

}