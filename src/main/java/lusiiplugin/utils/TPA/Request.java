package lusiiplugin.utils.TPA;

public class Request {
	public final RequestType type;
	public final String user;

    public Request(RequestType type, String user) {
        this.type = type;
        this.user = user;
    }

	@Override
	public String toString() {
		return "§1[ §3" + user + " §1| §4" + type.toString() + " §1]§r";
	}
}

