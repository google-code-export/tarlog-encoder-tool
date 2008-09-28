package tarlog.encoder.tool.api.fields;


public interface ListConverter<T> {

    String[] toList(T[] list);
    
    T[] fromList(String[] list);
    
}
