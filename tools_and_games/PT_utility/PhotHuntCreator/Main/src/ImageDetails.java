/**
 * Created by SiongLeng on 6/4/2016.
 */
public class ImageDetails {

    private CorrectArea area1;
    private CorrectArea area2;
    private CorrectArea area3;
    private CorrectArea area4;
    private CorrectArea area5;
    private int width;
    private int height;
    private int index;
    private String imageOneUrl;
    private String imageTwoUrl;
    private String id;

    public ImageDetails(String id, CorrectArea area1, CorrectArea area2, CorrectArea area3, CorrectArea area4, CorrectArea area5, int width, int height, int index) {
        this.id = id;
        this.area1 = area1;
        this.area2 = area2;
        this.area3 = area3;
        this.area4 = area4;
        this.area5 = area5;
        this.width = width;
        this.height = height;
        this.index = index;
    }

    public ImageDetails() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CorrectArea getArea1() {
        return area1;
    }

    public void setArea1(CorrectArea area1) {
        this.area1 = area1;
    }

    public CorrectArea getArea2() {
        return area2;
    }

    public void setArea2(CorrectArea area2) {
        this.area2 = area2;
    }

    public CorrectArea getArea3() {
        return area3;
    }

    public void setArea3(CorrectArea area3) {
        this.area3 = area3;
    }

    public CorrectArea getArea4() {
        return area4;
    }

    public void setArea4(CorrectArea area4) {
        this.area4 = area4;
    }

    public CorrectArea getArea5() {
        return area5;
    }

    public void setArea5(CorrectArea area5) {
        this.area5 = area5;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getImageTwoUrl() {
        return imageTwoUrl;
    }

    public void setImageTwoUrl(String imageTwoUrl) {
        this.imageTwoUrl = imageTwoUrl;
    }

    public String getImageOneUrl() {
        return imageOneUrl;
    }

    public void setImageOneUrl(String imageOneUrl) {
        this.imageOneUrl = imageOneUrl;
    }
}
