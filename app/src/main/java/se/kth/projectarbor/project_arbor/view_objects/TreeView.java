package se.kth.projectarbor.project_arbor.view_objects;

/**
 * Created by Patrik on 2017-05-15.
 */

        import android.content.Context;

        import android.graphics.Bitmap;

        import android.graphics.Color;
        import android.graphics.drawable.Drawable;
        import android.os.Handler;

        import android.util.AttributeSet;

        import android.util.Log;

        import android.widget.ImageView;

        import se.kth.projectarbor.project_arbor.R;


public class TreeView extends ImageView implements Runnable {



    private static final String TAG = "GifDecoderView";

    private GifDecoder gifDecoder;

    private Bitmap tmpBitmap;

    private final Handler handler = new Handler();

    private boolean animating = false;

    private Thread animationThread;

    private final Runnable updateResults = new Runnable() {

        @Override

        public void run() {

            if (tmpBitmap != null && !tmpBitmap.isRecycled()) {

                setBackgroundResource(R.color.colorTransparent);
                setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                setImageBitmap(tmpBitmap);
                setScaleX(1.3f);
                setScaleY(1.3f);

            }

        }

    };



    public TreeView(final Context context, final AttributeSet attrs) {

        super(context, attrs);

    }



    public TreeView(final Context context) {

        super(context);

    }



    public void setBytes(final byte[] bytes) {

        gifDecoder = new GifDecoder();

        try {

            gifDecoder.read(bytes);

        } catch (final OutOfMemoryError e) {

            gifDecoder = null;

            return;

        }



        if (canStart()) {

            animationThread = new Thread(this);

            animationThread.start();

        }

    }



    public void startAnimation() {

        animating = true;



        if (canStart()) {

            animationThread = new Thread(this);

            animationThread.start();

        }

    }



    public void stopAnimation() {

        animating = false;



        if (animationThread != null) {

            animationThread.interrupt();

            animationThread = null;

        }

    }



    private boolean canStart() {

        return animating && gifDecoder != null && animationThread == null;

    }



    @Override

    public void run() {

        final int n = gifDecoder.getFrameCount();

        do {

            for (int i = 0; i < n; i++) {

                try {

                    tmpBitmap = gifDecoder.getNextFrame();

                    handler.post(updateResults);

                } catch (final ArrayIndexOutOfBoundsException e) {

                    Log.w(TAG, e);

                } catch (final IllegalArgumentException e) {

                    Log.w(TAG, e);

                }

                gifDecoder.advance();

                try {

                    Thread.sleep(gifDecoder.getNextDelay());

                } catch (final InterruptedException e) {

                    // suppress

                }

            }

        } while (animating);

    }

}
