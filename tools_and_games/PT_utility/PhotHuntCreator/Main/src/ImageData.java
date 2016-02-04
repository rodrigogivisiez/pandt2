/**
 * Created by SiongLeng on 3/2/2016.
 */
public class ImageData {

    private String json;
    private long index;
    private String id;

    public ImageData() {
    }

    public ImageData(String json, long index, String id) {
        this.json = json;
        this.index = index;
        this.id = id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
