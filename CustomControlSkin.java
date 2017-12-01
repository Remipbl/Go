import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;

public class CustomControlSkin extends SkinBase<CustomControl> implements Skin<CustomControl> {
    // Default constructor
    public CustomControlSkin(CustomControl gc) {
        super(gc);
    }
}
