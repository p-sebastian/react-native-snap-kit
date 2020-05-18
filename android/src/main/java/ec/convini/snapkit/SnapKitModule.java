package ec.convini.snapkit;

import android.content.res.AssetManager;
import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.snapchat.kit.sdk.SnapCreative;
import com.snapchat.kit.sdk.creative.api.SnapCreativeKitApi;
import com.snapchat.kit.sdk.creative.exceptions.SnapStickerSizeException;
import com.snapchat.kit.sdk.creative.media.SnapMediaFactory;
import com.snapchat.kit.sdk.creative.media.SnapSticker;
import com.snapchat.kit.sdk.creative.models.SnapLiveCameraContent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SnapKitModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public SnapKitModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "SnapKit";
    }

    @ReactMethod
    public void sendSticker(String assetName, String url, float width, float height, Promise promise) {
        SnapCreativeKitApi creativeKitApi = SnapCreative.getApi(reactContext);
        SnapMediaFactory factory = SnapCreative.getMediaFactory(reactContext);
        SnapLiveCameraContent content = new SnapLiveCameraContent();
        SnapSticker sticker;
        try {
            File file = getFileFromAsset(assetName);
            sticker = factory.getSnapStickerFromFile(file);
        } catch (SnapStickerSizeException | IOException e) {
            promise.reject(e.getMessage());
            return;
        }

        // Position is specified as a ratio between 0 & 1 to place the center of the sticker
        sticker.setHeight(height);
        sticker.setWidth(width);
        sticker.setPosX(0.5f);
        sticker.setPosY(0.5f);
        content.setSnapSticker(sticker);
        content.setAttachmentUrl(url);
        creativeKitApi.send(content);
        promise.resolve(null);
    }
    private File getFileFromAsset(String assetName) throws IOException {
        File dir = getReactApplicationContext().getCacheDir();
        // Save asset to temp File, because Asset cant be used as `File`
        File file = File.createTempFile("snap", assetName, dir);
        FileOutputStream fos = null;
        InputStream stream = null;
        try {
            AssetManager assets = getReactApplicationContext().getAssets();
            stream = assets.open(assetName);
            byte[] data = new byte[stream.available()];
            int nbread = 0;
            fos = new FileOutputStream(file);
            while((nbread=stream.read(data))> -1) {
                fos.write(data, 0, nbread);
            }
            // return temp file which is a copy of asset
            return file;
        } catch (IOException e) {
            throw e;
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (stream != null) {
                stream.close();
            }
        }
    }
}
