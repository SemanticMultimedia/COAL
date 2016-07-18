package org.s16a.mcas.util;

public class TrackInformation {
    private final String artist;
    private final String title;
    private final String release;
    private final String musicbrainzId;
    private final byte[] artwork;
    private final String isrc;

    public TrackInformation(String artist, String title, String release, String musicbrainzid, byte[] artwork, String isrc) {
        this.artist = artist;
        this.title = title;
        this.release = release;
        this.musicbrainzId = musicbrainzid;
        this.artwork = artwork;
        this.isrc = isrc;
    }

    public String getArtist() {
        return artist;
    }

    public byte[] getArtwork() {
        return artwork;
    }

    public String getIsrc() {
        return isrc;
    }

    public String getMusicbrainzid() {
        return musicbrainzId;
    }

    public String getRelease() {
        return release;
    }

    public String getTitle() {
        return title;
    }

    public String toString() {
        String result = "";

        if (this.title != null)
            result += this.title + "\n";

        if (this.artist != null)
            result += this.artist + "\n";

        if (this.release != null)
            result += this.release + "\n";

        if (this.artwork != null)
            result += new String(this.artwork) + "\n";

        if (this.musicbrainzId != null)
            result += this.musicbrainzId + "\n";

        if (this.isrc != null)
            result += this.isrc + "\n";

        return result;

    }
}
