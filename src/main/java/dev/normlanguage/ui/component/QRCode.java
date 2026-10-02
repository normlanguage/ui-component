package dev.normlanguage.ui.component;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
public class QRCode extends ImageView {
    private String text;
    private int size;
    public QRCode(String text, int size) { setText(text); setSize(size); getStyleClass().add("norm-qrcode"); }
    public String getText() { return text; }
    public int getSize() { return size; }
    public void setText(String value) { text = value; render(); }
    public void setSize(int value) { if (value <= 0) throw new IllegalArgumentException("size"); size = value; render(); }
    private void render() {
        if (text == null || size <= 0) return;
        try {
            var matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size);
            var pixels = new WritableImage(size, size);
            var writer = pixels.getPixelWriter();
            for (int y = 0; y < size; y++) for (int x = 0; x < size; x++)
                writer.setArgb(x, y, matrix.get(x,y) ? 0xff000000 : 0xffffffff);
            setImage(pixels);
        } catch (WriterException error) { throw new IllegalArgumentException("Invalid QR content", error); }
    }
}
