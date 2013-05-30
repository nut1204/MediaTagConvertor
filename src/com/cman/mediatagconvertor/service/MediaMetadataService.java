package com.cman.mediatagconvertor.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.io.TextEncoding;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;
import android.media.MediaMetadataRetriever;
import com.cman.mediatagconvertor.model.MediaMetadata;
import com.cman.mediatagconvertor.model.MediaMetadataWithFile;

public class MediaMetadataService {

	public List<MediaMetadataWithFile> getListMediaMetadata(List<File> files) {
		ArrayList<MediaMetadataWithFile> mediaMetadataList = new ArrayList<MediaMetadataWithFile>();
		MediaMetadataWithFile mediaMetadata;
		for (File file : files) {
			mediaMetadata = new MediaMetadataWithFile(file.getAbsolutePath());
			extractMetadata(mediaMetadata);
			mediaMetadataList.add(mediaMetadata);
		}
		return mediaMetadataList;
	}

	public void saveMediaMetadata(MediaMetadataWithFile mediaMetadataWithFile,
			String encoding) throws Exception {

		File file = mediaMetadataWithFile.getFile();
		EncodingService encodingService = new EncodingService(encoding);
		MediaMetadata mediaMetadata = encodingService
				.getMediaMetadataWithEncoding(mediaMetadataWithFile);

		MediaFile mediaFile = new MP3File(file);
		ID3V2_3_0Tag oID3V2_3_0Tag = new ID3V2_3_0Tag();
		TextEncoding.setDefaultTextEncoding(TextEncoding.UNICODE);

		// oID3V2_3_0Tag.setAlbum("Album");
		// oID3V2_3_0Tag.setGenre("Blues");
		oID3V2_3_0Tag.setArtist(mediaMetadata.getArtist());
		oID3V2_3_0Tag.setTitle(mediaMetadata.getTitle());
		mediaFile.setID3Tag(oID3V2_3_0Tag);

		// update the actual file to reflect the current state of our object
		mediaFile.sync();
	}

	public void extractMetadata(MediaMetadataWithFile mediaMetadata) {
		MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		metaRetriever.setDataSource(mediaMetadata.getFile().getAbsolutePath());

		byte[] image;
		String title, album, artist;
		title = metaRetriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		album = metaRetriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		artist = metaRetriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		image = metaRetriever.getEmbeddedPicture();
		
		mediaMetadata.setTitle(title);
		mediaMetadata.setAlbum(album);
		mediaMetadata.setArtist(artist);
		mediaMetadata.setImage(image);

		validateMetadata(mediaMetadata);
		metaRetriever.release();
	}

	private void validateMetadata(MediaMetadataWithFile mediaMetadata) {
		String title = mediaMetadata.getTitle();
		String artist = mediaMetadata.getArtist();

		if (title == null || title.isEmpty()) {
			String name = mediaMetadata.getFile().getName();
			int pos = name.lastIndexOf(".");
			if (pos > 0) {
				name = name.substring(0, pos);
			}
			mediaMetadata.setTitle(name);
		}

		if (artist == null || artist.isEmpty()) {
			mediaMetadata.setArtist("Unknown artist");
		}
	}
}
