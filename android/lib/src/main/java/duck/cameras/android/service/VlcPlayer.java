package duck.cameras.android.service;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceView;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;

public class VlcPlayer {

    private final int defaultVolume = 100;
    private final MediaPlayer player;
    private final EventListener listener;

    public VlcPlayer(Context context, EventListener listener, SurfaceView surfaceView) {
        this.listener = listener;

        player = new MediaPlayer(new LibVLC(context));
        player.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                if (event.type == MediaPlayer.Event.Vout) {
                    listener.onVlcPlayerEvent(Event.STREAM_STARTED);
                }
            }
        });
        player.setVideoTrackEnabled(true);

        IVLCVout vlcOut = player.getVLCVout();
        vlcOut.setVideoView(surfaceView);
        vlcOut.setWindowSize(surfaceView.getWidth(), surfaceView.getHeight());
        vlcOut.attachViews();
    }

    public void play(String url) {
        Uri uri = Uri.parse(NetworkService.processUrl(url));
        Media media = new Media(player.getLibVLC(), uri);
        media.setHWDecoderEnabled(true, false);
        player.setMedia(media);
        player.play();
    }

    public void pause() {
        player.pause();
    }

    public void stop() {
        player.stop();
    }

    public boolean isMuted() {
        return player.getVolume() == 0;
    }

    public void mute() {
        player.setVolume(0);
    }

    public void unmute() {
        player.setVolume(defaultVolume);
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public enum Event {
        STREAM_STARTED
    }

    public interface EventListener {
        void onVlcPlayerEvent(Event event);
    }
}
