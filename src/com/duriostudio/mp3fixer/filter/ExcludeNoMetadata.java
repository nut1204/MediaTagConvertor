package com.duriostudio.mp3fixer.filter;

import com.duriostudio.mp3fixer.model.MediaMetadataWithFile;

public class ExcludeNoMetadata implements IMediaMetadataFilter {

	@Override
	public boolean getFilter(MediaMetadataWithFile mediaMetadataWithFile) {
		String title, artist, album;
		title = mediaMetadataWithFile.getTitle();
		artist = mediaMetadataWithFile.getArtist();
		album = mediaMetadataWithFile.getAlbum();
		
		if(title == null && artist == null && album == null){
			return false;
		}
		return true;
	}

}
