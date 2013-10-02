package b;

interface AliceReaderListener {
  // modified: added String topic argument, <topic> support (Drent 10-13-1999)
  public void newCategory(String pattern, String that, String topic, String template);
}
