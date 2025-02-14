package de.danoeh.antennapod.core.feed.util;

import android.util.Log;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;
import de.danoeh.antennapod.model.feed.FeedPreferences;
import de.danoeh.antennapod.model.playback.MediaType;
import de.danoeh.antennapod.core.preferences.PlaybackPreferences;
import de.danoeh.antennapod.storage.preferences.UserPreferences;
import de.danoeh.antennapod.model.playback.Playable;

import static de.danoeh.antennapod.model.feed.FeedPreferences.SPEED_USE_GLOBAL;

/**
 * Utility class to use the appropriate playback speed based on {@link PlaybackPreferences}
 */
public final class PlaybackSpeedUtils {
    private static final String TAG = "PlaybackSpeedUtils";

    private PlaybackSpeedUtils() {
    }

    /**
     * Returns the currently configured playback speed for the specified media.
     */
    public static float getCurrentPlaybackSpeed(Playable media) {
        float playbackSpeed = SPEED_USE_GLOBAL;
        MediaType mediaType = null;

        if (media != null) {
            mediaType = media.getMediaType();
            playbackSpeed = PlaybackPreferences.getCurrentlyPlayingTemporaryPlaybackSpeed();

            if (playbackSpeed == SPEED_USE_GLOBAL && media instanceof FeedMedia) {
                FeedItem item = ((FeedMedia) media).getItem();
                if (item != null) {
                    Feed feed = item.getFeed();
                    if (feed != null && feed.getPreferences() != null) {
                        playbackSpeed = feed.getPreferences().getFeedPlaybackSpeed();
                    } else {
                        Log.d(TAG, "Can not get feed specific playback speed: " + feed);
                    }
                }
            }
        }

        if (playbackSpeed == SPEED_USE_GLOBAL) {
            playbackSpeed = UserPreferences.getPlaybackSpeed(mediaType);
        }

        return playbackSpeed;
    }

    /**
     * Returns the currently configured skip silence for the specified media.
     */
    public static FeedPreferences.SkipSilence getCurrentSkipSilencePreference(Playable media) {
        FeedPreferences.SkipSilence skipSilence = FeedPreferences.SkipSilence.GLOBAL;
        if (media != null) {
            skipSilence = PlaybackPreferences.getCurrentlyPlayingTemporarySkipSilence();
            if (skipSilence == FeedPreferences.SkipSilence.GLOBAL && media instanceof FeedMedia) {
                FeedItem item = ((FeedMedia) media).getItem();
                if (item != null && item.getFeed() != null && item.getFeed().getPreferences() != null) {
                    skipSilence = item.getFeed().getPreferences().getFeedSkipSilence();
                }
            }
        }
        if (skipSilence == FeedPreferences.SkipSilence.GLOBAL) {
            skipSilence = UserPreferences.isSkipSilence()
                    ? FeedPreferences.SkipSilence.AGGRESSIVE : FeedPreferences.SkipSilence.OFF;
        }
        return skipSilence;
    }
}
