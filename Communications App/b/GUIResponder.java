package b;

public class GUIResponder implements Responder {
  public String pre_process(String input, String hname) {
    return input;
  }
  public void log(String input, String response, String hname) {
  }
  public String append(String input, String response, String scroll) {
    response = Substituter.suppress_html(response);
    response = Substituter.wrapText(response, 80);
    scroll = scroll + response + "\n";
    return scroll;
  }
  public String post_process(String reply) {
    Classifier.toFile();
    return reply;
  }
} // GUIResponder