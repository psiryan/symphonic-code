/***************************************************************************
 *   Copyright (C) 2007 by Sylvain Paré                                    *
 *   garthps@users.sourceforge.net                                         *
 *                                                                         *
 *   The original work was from OtiasJ from the project ml_sony under BSD  *
 *   licence. Huge thanks to him ;)                                        *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

#include <list>
#include "CommonDefines.h"
#include "NativeMethods.h"


static int STRCMP2_NULLOK ( const char *pa, const char *pb )
{
	if ( !pa ) pa="";
	if ( !pb ) pb="";
	return _stricmp ( pa,pb );
}

static int STRNCMP_NULLOK ( const char *pa, const char *pb, int size )
{
	if ( !pa ) pa="";
	if ( !pb ) pb="";
	return _strnicmp ( pa, pb, size );
}


//Read one track info from 04CNTINF
bool getTrack ( FILE *f, Song *song )
{
	TrackHeader t;

	//get the track info
	if ( fread ( &t, sizeof ( TrackHeader ), 1, f ) != 1 )
	{
		LOG ( "error could not read the TrackHeader\n" );
		return false;
	}
	else
	{
		if ( t.nbTagRecords == 0 ) //last track reached
		{
			LOG ( "Last track reached" );
			return false;
		}

		//get values from big endian
		t.trackEncoding = UINT32_SWAP_BE_LE ( t.trackEncoding );
		t.trackLength = UINT32_SWAP_BE_LE ( t.trackLength );
		t.nbTagRecords = UINT16_SWAP_BE_LE ( t.nbTagRecords );
		t.sizeTagRecords = UINT16_SWAP_BE_LE ( t.sizeTagRecords );
	}

	//get song encoding type
	song->setEncoding ( t.trackEncoding );

	//length
	song->setLength ( t.trackLength );


	//read the tags from the 04CNTINF.DAT file
	char tagType[4];
	char encoding[2];
	utf16char *tagRecord = ( utf16char* ) malloc ( sizeof ( utf16char ) * t.sizeTagRecords );

	//check if the track is empty
	bool isEmpty = true;
	song->setStatus ( Song::EMPTYTRACK );

	for ( int i = 1; i <= t.nbTagRecords; i++ )
	{
		//read type
		if ( fread ( tagType, 4, 1, f ) != 1 )
		{
			LOG ( "Could not read track type" );
			return false;
		}

		//read encoding
		if ( fread ( encoding, 2, 1, f ) != 1 )
		{
			LOG ( "Could not read track encoding" );
			return false;
		}

		//read the tag
		if ( fread ( tagRecord, t.sizeTagRecords - 6, 1, f ) != 1 )
		{
			LOG ( "Could not read track tag" );
			return false;
		}
		else
		{
			//we have a tag check if it's empty
			for ( int j = 0; j < t.sizeTagRecords - 6; j++ )
			{
				if ( tagRecord[j] != 0 )
					isEmpty = false;
			}

			if ( isEmpty )
				continue;
		}

		if ( STRNCMP_NULLOK ( tagType, "TIT2", 4 ) == 0 )
		{
			song->setTitle ( utf16_to_ansi ( ( utf16char* ) tagRecord, t.sizeTagRecords, true ) );
			continue;
		}

		if ( STRNCMP_NULLOK ( tagType, "TPE1", 4 ) == 0 )
		{
			song->setArtist ( utf16_to_ansi ( ( utf16char* ) tagRecord, t.sizeTagRecords, true ) );
			continue;
		}

		if ( STRNCMP_NULLOK ( tagType, "TALB", 4 ) == 0 )
		{
			song->setAlbum ( utf16_to_ansi ( ( utf16char* ) tagRecord, t.sizeTagRecords, true ) );
			continue;
		}

		if ( STRNCMP_NULLOK ( tagType, "TCON", 4 ) == 0 )
		{
			song->setGenre ( utf16_to_ansi ( ( utf16char* ) tagRecord, t.sizeTagRecords, true ) );
			continue;
		}
	}

	if ( !isEmpty )
		song->setStatus ( Song::PRESENT );

	return ( true );
}

//read all tracks from the device to a song vector (not used anymore using getTrackInfo instead)
ResultCode read_04CNTINF ( string *path, list<Song *> *songs )
{
	FILE *f;
	Song *s;

	//open the file
	f = fopen ( path->c_str(), "rb" );
	if ( f == NULL )
	{
		LOG ( "error can't open file 04CNTINF.DAT" );
		return FAILED;
	}

	//read the headers
	FileHeader header;
	ObjectPointer Opointer;
	ObjectHeader obj;

	if ( !getHeader ( &header, f ) )
	{
		fclose ( f );
		LOG ( "could not read 04CNTINF file header\n" );
		return FAILED;
	}
	if ( !getObjectPointer ( &Opointer, f ) )
	{
		fclose ( f );
		LOG ( "could not read 04CNTINF Object pointer\n" );
		return FAILED;
	}
	if ( !getObjectHeader ( &obj, f ) )
	{
		fclose ( f );
		LOG ( "could not read 04CNTINF Object header\n" );
		return FAILED;
	}

	for ( int index = 1; index <= obj.count; index++ )
	{
		s = new Song();

		if ( getTrack ( f, s ) )
		{
			s->setDbIndex ( index );
			songs->push_back ( s );
		}
	}
	fclose ( f );
	return ( SUCCESS );
}


//read all the playlist names from the device
ResultCode read03GINF22 ( string *path, list<Songlist *> *playlists )
{
	FILE *f;

	//open the file
	f = fopen ( path->c_str(), "rb" );
	if ( f == NULL )
	{
		LOG ( "error can't open file 03GINF22.DAT" );
		return FAILED;
	}

	//read the headers
	FileHeader header;
	ObjectPointer Opointer;
	ObjectHeader obj;

	if ( !getHeader ( &header, f ) )
	{
		fclose ( f );
		return FAILED;
	}
	if ( !getObjectPointer ( &Opointer, f ) )
	{
		fclose ( f );
		return FAILED;
	}
	if ( !getObjectHeader ( &obj, f ) )
	{
		fclose ( f );
		return FAILED;
	}

	Song *taginfo;
	//get the playlist names
	for ( int index = 1; index <= obj.count; index++ )
	{
		taginfo = new Song(); //so that the tags are reseted each time

		if ( getTrack ( f, taginfo ) )
		{
			Songlist *p = new Songlist ( taginfo, index, Songlist::SORT_PLAYLIST );
			playlists->push_back ( p );
		}
		delete taginfo; //so that the tags are reseted each time
	}

	fclose ( f );
	if ( playlists->size() > 0 )
		return ( SUCCESS );
	else
		return FAILED;
}

//read songs playlist correspondance from the device
ResultCode read01TREE22 ( string *path, list<Song*> *songsByDbIndex, list<Songlist *> *playlists )
{
	if ( path == NULL )
		return INVALID_PARAMETER;

	if ( songsByDbIndex == NULL )
		return INVALID_PARAMETER_1;

	if ( playlists == NULL )
		return INVALID_PARAMETER_2;

	FILE *f;

	//open the file
	f = fopen ( path->c_str(), "rb" );
	if ( f == NULL )
	{
		LOG ( "error can't open file 01TREE22.DAT" );
		return FAILED;
	}

	//read the headers
	FileHeader header;
	ObjectPointer Opointer_GPLB;
	ObjectPointer Opointer_TPLB;
	ObjectHeader obj_GPLB;
	ObjectHeader obj_TPLB;

	//TREE header
	if ( !getHeader ( &header, f ) )
	{
		fclose ( f );
		return FAILED;
	}
	//GPLB pointer
	if ( !getObjectPointer ( &Opointer_GPLB, f ) )
	{
		fclose ( f );
		return FAILED;
	}
	//TPLB pointer
	if ( !getObjectPointer ( &Opointer_TPLB, f ) )
	{
		fclose ( f );
		return FAILED;
	}
	//GPLB header
	if ( !getObjectHeader ( &obj_GPLB, f ) )
	{
		fclose ( f );
		return FAILED;
	}

	uint16 index1;
	uint16 index2;
	uint16 cte1;
	uint16 cte2;

	list<uint16> indexTPLB;
	list<Songlist*> sortedList;

	//GPLB object
	for ( int i = 0; i < obj_GPLB.count; i++ )
	{
		fread ( &index1, sizeof ( uint16 ), 1, f );
		fread ( &cte1, sizeof ( uint16 ), 1, f );
		fread ( &index2, sizeof ( uint16 ), 1, f );
		fread ( &cte2, sizeof ( uint16 ), 1, f );
		index1 = UINT16_SWAP_BE_LE ( index1 );
		index2 = UINT16_SWAP_BE_LE ( index2 );

		//sort the playlist in this file order
		for ( list<Songlist*>::iterator playlist = playlists->begin(); playlist != playlists->end(); playlist++ )
		{
			if ( ( *playlist )->getIndex() == index1 )
			{
				sortedList.push_back ( *playlist );
				break;
			}
		}
		indexTPLB.push_back ( index2 );
	}

	if ( indexTPLB.size() <= 0 )
	{
		fclose ( f );
		return FAILED;
	}

	//seek TPLB object
	fseek ( f, Opointer_TPLB.offset, SEEK_SET );

	//TPLB header
	if ( !getObjectHeader ( &obj_TPLB, f ) )
	{
		fclose ( f );
		return FAILED;
	}

	list<Songlist*>::iterator playlist = sortedList.begin();
	int playlistIndex = 0;

	int nextTPLBindex;

	//2 or more playlist
	if ( indexTPLB.size() >= 2 )
	{
		indexTPLB.pop_front();
		nextTPLBindex = indexTPLB.front();
		indexTPLB.pop_front();
	}
	else
	{
		//put everything in the first playlist
		nextTPLBindex = -1;
	}


	int dbIndex = 0;
	Song* song;

	for ( int j = 1; j <= obj_TPLB.count; j++ )
	{
		//read the dbIndex
		fread ( &dbIndex, sizeof ( uint16 ), 1, f );
		dbIndex = UINT16_SWAP_BE_LE ( dbIndex );

		//find the song
		song = NULL;
		for ( list<Song*>::iterator s = songsByDbIndex->begin(); s != songsByDbIndex->end(); s++ )
		{
			if ( ( *s )->getDbIndex() == dbIndex )
			{
				song = *s;
				break;
			}
		}

		//add the song to the playlist
		if ( song != NULL )
		{
			( *playlist )->songs->push_back ( song );
			( *playlist )->setLength ( ( *playlist )->getLength() + song->getLength() );
		}

		if ( j == nextTPLBindex )
		{
			//change to next playlist
			playlist++;
			playlistIndex++;
			if ( indexTPLB.size() >= 1 )
			{
				nextTPLBindex = indexTPLB.front();
				indexTPLB.pop_front();
			}
			else
			{
				nextTPLBindex = -1;
			}
		}
	}

	fclose ( f );

	return ( SUCCESS );
}



//read the track's tags directly from the omg header
ResultCode getTrackInfo ( char *path, Song *s )
{
	FILE *fin = fopen ( path, "rb" );
	if ( fin == NULL )
	{
		LOG ( "error can't open file" );
		LOG ( path );
		LOG ( "\n" );
		return ( FAILED );
	}

	uint8     tmpTag[512];
	uint32    tagLength = 0;
	uint32    firstHeaderLength = 0;

	//read the id3 header
	if ( fread ( &tmpTag, sizeof ( uint8 ), 10, fin ) != 10 )
	{
		LOG ( "error can't read id3 header" );
		fclose ( fin );
		return ( FAILED );
	}

	//this header is in SYNCH SAFE format
	firstHeaderLength = ( ( tmpTag[6] << 21 ) + ( tmpTag[7] << 14 ) + ( tmpTag[8] << 7 ) + tmpTag[9] ) - 1;

	memset ( tmpTag, 0, 512 );
	if ( fread ( &tmpTag, sizeof ( uint8 ), 11, fin ) != 11 )
	{
		LOG ( "error could not read the tag type and length" );
		fclose ( fin );
		return ( FAILED );
	}

	//check tag type
	while ( ( tmpTag[4] != 0 ) || ( tmpTag[5] != 0 ) || ( tmpTag[6] != 0 ) || ( tmpTag[7] != 0 ) )
	{
		//This one not synch safe!
		//get tag length :
		tagLength = ( ( tmpTag[4] << 24 ) + ( tmpTag[5] << 16 ) + ( tmpTag[6] << 8 ) + tmpTag[7] ) - 1;

		if ( ( STRNCMP_NULLOK ( ( char* ) tmpTag, "TIT2", 4 ) == 0 ) )
		{
			memset ( tmpTag, 0, 512 );
			if ( fread ( &tmpTag, sizeof ( uint8 ), tagLength, fin ) != tagLength )
			{
				fclose ( fin );
				return ( FAILED );
			}
			char *title = utf16_to_ansi ( ( uint16* ) tmpTag, tagLength, true );
			s->setTitle ( title );
			free ( title );
		}
		else if ( ( STRNCMP_NULLOK ( ( char* ) tmpTag, "TPE1", 4 ) == 0 ) )
		{
			memset ( tmpTag, 0, 512 );
			if ( fread ( &tmpTag, sizeof ( uint8 ), tagLength, fin ) != tagLength )
			{
				fclose ( fin );
				return ( FAILED );
			}
			char *artist = utf16_to_ansi ( ( uint16* ) tmpTag, tagLength, true );
			s->setArtist ( artist );
			free ( artist );
		}
		else if ( ( STRNCMP_NULLOK ( ( char* ) tmpTag, "TALB", 4 ) == 0 ) )
		{
			memset ( tmpTag, 0, 512 );
			if ( fread ( &tmpTag, sizeof ( uint8 ), tagLength, fin ) != tagLength )
			{
				fclose ( fin );
				return ( FAILED );
			}
			char *album = utf16_to_ansi ( ( uint16* ) tmpTag, tagLength, true );
			s->setAlbum ( album );
			free ( album );
		}
		else if ( ( STRNCMP_NULLOK ( ( char* ) tmpTag, "TCON", 4 ) == 0 ) )
		{
			memset ( tmpTag, 0, 512 );
			if ( fread ( &tmpTag, sizeof ( uint8 ), tagLength, fin ) != tagLength )
			{
				fclose ( fin );
				return ( FAILED );
			}
			char *genre = utf16_to_ansi ( ( uint16* ) tmpTag, tagLength, true );
			s->setGenre ( genre );
			free ( genre );
		}
		else if ( ( STRNCMP_NULLOK ( ( char* ) tmpTag, "TYER", 4 ) == 0 ) )
		{
			memset ( tmpTag, 0, 512 );
			if ( fread ( &tmpTag, sizeof ( uint8 ), tagLength, fin ) != tagLength )
			{
				fclose ( fin );
				return ( FAILED );
			}
			char *year = utf16_to_ansi ( ( uint16* ) tmpTag, tagLength, true );
			s->setYear ( atoi ( year ) );
			free ( year );
		}
		else if ( ( STRNCMP_NULLOK ( ( char* ) tmpTag, "TXXX", 4 ) == 0 ) )
		{
			memset ( tmpTag, 0, 512 );
			if ( fread ( &tmpTag, sizeof ( uint8 ), tagLength, fin ) != tagLength )
			{
				fclose ( fin );
				return ( FAILED );
			}

			tmpTag[19] = 32;//quick fix : replace '*' by a space in "OMG_TRACK*XXXX"
			char *res1 = utf16_to_ansi ( ( uint16* ) tmpTag, tagLength, true );
			char *res2 = res1 + 10; //skip OMG_TRACK or OMG_TRLDA
			if ( STRNCMP_NULLOK ( res1, "OMG_TRACK", 9 ) == 0 )
			{
				s->setTrack ( atoi ( res2 ) );
			}
			else if ( STRNCMP_NULLOK ( res1, "OMG_TRLDA", 9 ) == 0 )
			{
				s->setYear ( atoi ( res2 ) );
			}
			if ( res1 != NULL )
				free ( res1 );
		}
		else if ( ( STRNCMP_NULLOK ( ( char* ) tmpTag, "TLEN", 4 ) == 0 ) )
		{
			memset ( tmpTag, 0, 512 );
			if ( fread ( &tmpTag, sizeof ( uint8 ), tagLength, fin ) != tagLength )
			{
				fclose ( fin );
				return ( FAILED );
			}
			char *length = utf16_to_ansi ( ( uint16* ) tmpTag, tagLength, true );
			s->setLength ( atoi ( length ) );
			free ( length );
		}
		else if ( ( STRNCMP_NULLOK ( ( char* ) tmpTag, "LEN", 4 ) == 0 ) ) //FIX FOR ML_SONY BUGGY RELEASED!
		{
			tagLength = ( ( tmpTag[3] << 24 ) + ( tmpTag[4] << 16 ) + ( tmpTag[5] << 8 ) + tmpTag[6] ) - 2; //-2 for the lost byte
			char lostByte = tmpTag[7];

			memset ( tmpTag, 0, 512 );
			if ( fread ( &tmpTag, sizeof ( uint8 ), tagLength, fin ) != tagLength )
			{
				fclose ( fin );
				return ( FAILED );
			}
			//shift everything 1 byte right and reinsert the lost byte
			for ( int k = tagLength - 1; k > 0; k-- )
			{
				tmpTag[k] = tmpTag[k-1];
			}
			tmpTag[0] = lostByte;

			char *length = utf16_to_ansi ( ( uint16* ) tmpTag, tagLength, true );
			s->setLength ( atoi ( length ) );
			free ( length );
			//return (SUCCESS);
		}
		else //skip tag
		{
			if ( fseek ( fin, tagLength, SEEK_CUR ) != 0 )
			{
				fclose ( fin );
				return ( FAILED );
			}
		}

		//get next tag info :
		memset ( tmpTag, 0, 512 );
		if ( fread ( &tmpTag, sizeof ( uint8 ), 11, fin ) != 11 )
		{
			LOG ( "error could not read the tag type and length" );
			fclose ( fin );
			return ( FAILED );
		}
	}

	//go to the second header:
	if ( fseek ( fin, firstHeaderLength + 11, SEEK_SET ) != 0 )
	{
		fclose ( fin );
		return ( FAILED );
	}

	//Read the second header (starts with id3):
	//first line
	if ( fread ( &tmpTag, sizeof ( uint8 ), 16, fin ) != 16 )
	{
		LOG ( "error can't read id3 header" );
		fclose ( fin );
		return ( FAILED );
	}

	//values :
	//tmpTag[7];//0xff no encoding is used, 0xfe this track is encoded

	//read the second line
	if ( fread ( &tmpTag, sizeof ( uint8 ), 16, fin ) != 16 )
	{
		LOG ( "error can't read id3 header" );
		fclose ( fin );
		return ( FAILED );
	}

	//unknown values on this line

	//read the third line
	if ( fread ( &tmpTag, sizeof ( uint8 ), 16, fin ) != 16 )
	{
		LOG ( "error can't read id3 header" );
		fclose ( fin );
		return ( FAILED );
	}

	//encoding settings :
	/*tmpTag[0] = 0x03;// 3 = MP3
	tmpTag[1] =(isVBR) ? 0x90 : 0x80 ;// VBR = 90, CBR = 80
	tmpTag[2] = s->encoding;// mpeg version(2bits), layer version(2bits), bitrate(4bits)
	tmpTag[3] = 0x10;//?? fixme
	*/
	uint32 encoding = ( ( tmpTag[3] << 24 ) + ( tmpTag[2] << 16 ) + ( tmpTag[1] << 8 ) + tmpTag[0] );
	s->setEncoding ( UINT32_SWAP_BE_LE ( encoding ) );

	//this is the tracklength
	uint32 trackLength = ( ( tmpTag[7] << 24 ) + ( tmpTag[6] << 16 ) + ( tmpTag[5] << 8 ) + tmpTag[4] );
	s->setLength ( UINT32_SWAP_BE_LE ( trackLength ) );

	//this is the number of frames
	/*tmpTag[8] = (uint8) (((nbFrames) & (uint32) 0xff000000U) >> 24);
	tmpTag[9] = (uint8) (((nbFrames) & (uint32) 0x00ff0000U) >>  16);
	tmpTag[10] = (uint8) (((nbFrames) & (uint32) 0x0000ff00U) >>  8);
	tmpTag[11] = (uint8) ((nbFrames) & (uint32)  0x000000ffU);
	*/




	fclose ( fin );
	return ( SUCCESS );
}


ResultCode write_00GTRLST ( const char *path )
{
	if ( path == NULL )
		return INVALID_PARAMETER;

	//write the 00GTRLST.DAT file
	FILE *f;

	//open the file
	f = fopen ( path, "wb" );
	if ( f == NULL )
	{
		LOG ( "error can't open file" );
		LOG ( path );
		LOG ( "\n" );
		LOG ( "WRITE ERROR 1\n" );
		return FAILED;
	}

	//write the headers
	FileHeader header;
	ObjectPointer Opointer;
	ObjectHeader obj;

	//file header
	header.magic[0] = 'G';
	header.magic[1] = 'T';
	header.magic[2] = 'L';
	header.magic[3] = 'T';
	writeHeader ( &header, f, 2 );

	//object pointer 1
	Opointer.magic[0] = 'S';
	Opointer.magic[1] = 'Y';
	Opointer.magic[2] = 'S';
	Opointer.magic[3] = 'B';
	Opointer.offset = UINT32_SWAP_BE_LE ( 0X0030 );
	Opointer.length = UINT32_SWAP_BE_LE ( 0x0070 );
	writeObjectPointer ( &Opointer, f );

	//object pointer 2
	Opointer.magic[0] = 'G';
	Opointer.magic[1] = 'T';
	Opointer.magic[2] = 'L';
	Opointer.magic[3] = 'B';
	Opointer.offset = UINT32_SWAP_BE_LE ( 0X00A0 );
	Opointer.length = UINT32_SWAP_BE_LE ( 0x0AB0 );
	writeObjectPointer ( &Opointer, f );

	uint8 t[16];
	for ( int i = 0; i < 16; i++ )
		t[i] = 0;

	//write the SYSB object header
	obj.magic[0] = 'S';
	obj.magic[1] = 'Y';
	obj.magic[2] = 'S';
	obj.magic[3] = 'B';
	obj.count = UINT16_SWAP_BE_LE ( 1 );
	obj.size = UINT16_SWAP_BE_LE ( 80 );
	obj.padding[0] = 0x00D0;
	obj.padding[1] = 0;
	writeObject ( &obj, f );

	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );

	//write the GTLB object header
	obj.magic[0] = 'G';
	obj.magic[1] = 'T';
	obj.magic[2] = 'L';
	obj.magic[3] = 'B';
	obj.count = UINT16_SWAP_BE_LE ( 34 );
	obj.size = UINT16_SWAP_BE_LE ( 80 );
	obj.padding[0] = UINT32_SWAP_BE_LE ( 0x0005 );
	obj.padding[1] = 0x0003;
	writeObject ( &obj, f );



	//first block
	t[1] = 0x01;
	t[3] = 0x01;
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	t[3] = 0;
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	t[1] = 0;
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );

	//2nd
	t[1] = 0x02;
	t[3] = 0x03;
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	t[1] = 0x01;
	t[3] = 0;
	t[4] = 'T';
	t[5] = 'P';
	t[6] = 'E';
	t[7] = '1';
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	t[1] = 0;
	t[4] = 0;
	t[5] = 0;
	t[6] = 0;
	t[7] = 0;
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );

	//3rd
	t[1] = 0x03;
	t[3] = 0x03;
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	t[1] = 0x01;
	t[3] = 0;
	t[4] = 'T';
	t[5] = 'A';
	t[6] = 'L';
	t[7] = 'B';
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	t[1] = 0;
	t[4] = 0;
	t[5] = 0;
	t[6] = 0;
	t[7] = 0;
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );


	//4th
	t[1] = 0x04;
	t[3] = 0x03;
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	t[1] = 0x01;
	t[3] = 0;
	t[4] = 'T';
	t[5] = 'C';
	t[6] = 'O';
	t[7] = 'N';
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	t[1] = 0;
	t[4] = 0;
	t[5] = 0;
	t[6] = 0;
	t[7] = 0;
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );

	//5th
	t[1] = 0x22;
	t[3] = 0x02;
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	t[1] = 0;
	t[3] = 0;
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );
	fwrite ( &t, sizeof ( uint8 ), 16, f );

	for ( int j = 5; j < 34 ; j++ )
	{
		t[1] = ( uint8 ) j;
		fwrite ( &t, sizeof ( uint8 ), 16, f );
		t[1] = 0;
		fwrite ( &t, sizeof ( uint8 ), 16, f );
		fwrite ( &t, sizeof ( uint8 ), 16, f );
		fwrite ( &t, sizeof ( uint8 ), 16, f );
		fwrite ( &t, sizeof ( uint8 ), 16, f );
	}

	fclose ( f );
	return ( SUCCESS );
}



ResultCode write_01TREEXX ( const char *path, list<Song *> *songs, list<Songlist *> *categories, int sortedBy )
{
	if ( path == NULL )
		return INVALID_PARAMETER;

	if ( songs == NULL )
		return INVALID_PARAMETER_1;

	if ( categories == NULL )
		return INVALID_PARAMETER_2;

	//write the 01TREEXX.DAT file
	FILE *f;
	int nbSongs = 0;
	int nbCat = 0;

	if ( path == NULL )
		return INVALID_PARAMETER;

	if ( songs == NULL )
		return INVALID_PARAMETER_1;

	if ( categories == NULL )
		return INVALID_PARAMETER_2;

	//open the file
	f = fopen ( path, "wb" );
	if ( f == NULL )
	{
		LOG ( "error can't open file" );
		LOG ( path );
		LOG ( "\n" );
		LOG ( "WRITE ERROR 2\n" );
		return FAILED;
	}

	//number of non empty tracks
	for ( list<Song *>::iterator count = songs->begin(); count != songs->end(); count++ )
	{
		if ( ( *count )->getStatus() != Song::EMPTYTRACK )
			nbSongs++;
	}

	//number of non empty categories
	nbCat = ( int ) categories->size();

	//write the headers
	FileHeader header;
	ObjectPointer Opointer;
	ObjectHeader obj;

	memcpy ( header.magic, "TREE", 4 );
	writeHeader ( &header, f, 2 );

	//GPLB object pointer
	memcpy ( Opointer.magic, "GPLB", 4 );
	Opointer.offset = UINT32_SWAP_BE_LE ( 0X0030 );
	Opointer.length = UINT32_SWAP_BE_LE ( 16400 );// 8 * 2048 + 16 of header
	writeObjectPointer ( &Opointer, f );

	//TPLB object pointer
	memcpy ( Opointer.magic, "TPLB", 4 );
	Opointer.offset = UINT32_SWAP_BE_LE ( 0X4040 );
	Opointer.length = UINT32_SWAP_BE_LE ( 16 + ( nbSongs * 2 )  + ( 16 - ( nbSongs * 2 ) % 16 ) ); //TPLB are 2 byte long + 16 of TPLB header
	writeObjectPointer ( &Opointer, f );

	//write the GPLB object header
	memcpy ( obj.magic, "GPLB", 4 );
	obj.count = UINT16_SWAP_BE_LE ( nbCat );
	obj.size = UINT16_SWAP_BE_LE ( 8 );
	obj.padding[0] = UINT32_SWAP_BE_LE ( UINT16_SWAP_BE_LE ( obj.count ) );
	obj.padding[1] = 0;
	writeObject ( &obj, f );

	//write the GPLB object
	uint16 index1 = 0;
	uint16 cte1 = UINT16_SWAP_BE_LE ( 0x0100 );
	uint16 index2 = 0;
	uint16 cte2 = 0x0000;

	int index = 0; //categories index
	int indexTPLB = 1; // index in the TPLB array

	//for each categorie (album, artist, genre...)
	//GPLB ARRAY :
	for ( list<Songlist *>::iterator categorie = categories->begin(); categorie != categories->end(); categorie++ )
	{
		index++;
		//index1 = UINT16_SWAP_BE_LE(index);
		index1 = UINT16_SWAP_BE_LE ( ( int ) ( *categorie )->getIndex() );
		index2 = UINT16_SWAP_BE_LE ( indexTPLB );
		fwrite ( &index1, sizeof ( uint16 ), 1, f );
		fwrite ( &cte1, sizeof ( uint16 ), 1, f );
		fwrite ( &index2, sizeof ( uint16 ), 1, f );
		fwrite ( &cte2, sizeof ( uint16 ), 1, f );
		indexTPLB += ( int ) ( *categorie )->songs->size();
	}

	//fill the rest with zeros
	int last = ( 16384 - ( index * 8 ) );
	uint8 cte3 = 0;
	for ( int i = 0; i < last; i++ )
		fwrite ( &cte3, sizeof ( uint8 ), 1, f );

	//write the TPLB object header
	memcpy ( obj.magic, "TPLB", 4 );
	obj.count = UINT16_SWAP_BE_LE ( nbSongs );
	obj.size = UINT16_SWAP_BE_LE ( 2 );
	obj.padding[0] = UINT32_SWAP_BE_LE ( UINT16_SWAP_BE_LE ( obj.count ) );
	obj.padding[1] = 0;
	writeObject ( &obj, f );

	index = 0;
	index1 = 0;

	//TPLB ARRAY :
	for ( list<Song *>::iterator song = songs->begin(); song != songs->end(); song++ )
	{
		if ( ( *song )->getStatus() == Song::EMPTYTRACK )
			continue;

		index++;
		index1 = UINT16_SWAP_BE_LE ( ( *song )->getDbIndex() );
		fwrite ( &index1, sizeof ( uint16 ), 1, f );
	}

	//so that we have 8 byte round file
	index1 = 0;
	while ( index % 8 != 0 )
	{
		fwrite ( &index1, sizeof ( uint16 ), 1, f );
		index++;
	}

	fclose ( f );
	return ( SUCCESS );
}

//empty playlist
ResultCode write_01TREE22 ( const char *path )
{
	if ( path == NULL )
		return INVALID_PARAMETER;

	//write the 01TREE22.DAT file
	FILE *f;

	//open the file
	f = fopen ( path, "wb" );
	if ( f == NULL )
	{
		LOG ( "error can't open file" );
		LOG ( path );
		LOG ( "\n" );
		LOG ( "WRITE ERROR 3\n" );
		return FAILED;
	}

	//write the headers
	FileHeader header;
	ObjectPointer Opointer;
	ObjectHeader obj;

	memcpy ( header.magic, "TREE", 4 );
	writeHeader ( &header, f, 2 );

	//GPLB object pointer
	memcpy ( Opointer.magic, "GPLB", 4 );
	Opointer.offset = UINT32_SWAP_BE_LE ( 0X0030 );
	Opointer.length = UINT32_SWAP_BE_LE ( 16 );
	writeObjectPointer ( &Opointer, f );

	//TPLB object pointer
	memcpy ( Opointer.magic, "TPLB", 4 );
	Opointer.offset = UINT32_SWAP_BE_LE ( 0X0040 );
	Opointer.length = UINT32_SWAP_BE_LE ( 16 );
	writeObjectPointer ( &Opointer, f );

	//write the GPLB object header
	memcpy ( obj.magic, "GPLB", 4 );
	obj.count = 0;
	obj.size = UINT16_SWAP_BE_LE ( 8 );
	obj.padding[0] = UINT32_SWAP_BE_LE ( UINT16_SWAP_BE_LE ( obj.count ) );
	obj.padding[1] = 0;
	writeObject ( &obj, f );

	//write the TPLB object header
	memcpy ( obj.magic, "TPLB", 4 );
	obj.count = UINT16_SWAP_BE_LE ( 0 );
	obj.size = UINT16_SWAP_BE_LE ( 2 );
	obj.padding[0] = UINT32_SWAP_BE_LE ( UINT16_SWAP_BE_LE ( obj.count ) );
	obj.padding[1] = 0;
	writeObject ( &obj, f );

	fclose ( f );
	return ( SUCCESS );
}

ResultCode write_02TREINF ( const char *path )
{
	if ( path == NULL )
		return INVALID_PARAMETER;

	//write the write_02TREINF.DAT file
	FILE *f;
	//int nbTags = 0;

	//open the file
	f = fopen ( path, "wb" );
	if ( f == NULL )
	{
		LOG ( "error can't open file" );
		LOG ( path );
		LOG ( "\n" );
		LOG ( "WRITE ERROR 4\n" );
		return FAILED;
	}


	//write the headers
	FileHeader header;
	ObjectPointer Opointer;
	ObjectHeader obj;

	memcpy ( header.magic, "GTIF", 4 );
	writeHeader ( &header, f );

	memcpy ( Opointer.magic, "GTFB", 4 );
	Opointer.offset = UINT32_SWAP_BE_LE ( 0x0020 );
	Opointer.length = UINT32_SWAP_BE_LE ( 0x2410 );
	writeObjectPointer ( &Opointer, f );

	memcpy ( obj.magic, "GTFB", 4 );
	obj.count = UINT16_SWAP_BE_LE ( 34 );
	obj.size = UINT16_SWAP_BE_LE ( 0x0090 );;
	obj.padding[0] = 0;
	obj.padding[1] = 0;
	writeObject ( &obj, f );

	//write the tags
	//int nbWriten = 0;
	TrackHeader t1;
	TrackTag tt1;
	TrackHeader t2;
	TrackTag tt2;

	//not really a sonytrack, just using the same struct
	t1.fileType[0] = 0;
	t1.fileType[1] = 0;
	t1.fileType[2] = 0;
	t1.fileType[3] = 0;
	t1.trackLength = UINT32_SWAP_BE_LE ( 619127 );//?? unknown value (this is not really trackLenght)
	t1.trackEncoding = 0;
	memcpy ( tt1.tagType, "TIT2", 4 );
	t1.nbTagRecords = UINT16_SWAP_BE_LE ( 1 );
	t1.sizeTagRecords = UINT16_SWAP_BE_LE ( TAGSIZE );

	//encoding
	tt1.tagEncoding[0] = 0x00;
	tt1.tagEncoding[1] = 0x02;


	//Empty objects
	t2.fileType[0] = 0;
	t2.fileType[1] = 0;
	t2.fileType[2] = 0;
	t2.fileType[3] = 0;
	t2.trackLength = 0;
	t2.trackEncoding = 0;
	tt2.tagType[0] = 0;
	tt2.tagType[1] = 0;
	tt2.tagType[2] = 0;
	tt2.tagType[3] = 0;
	tt2.tagEncoding[0] = 0;
	tt2.tagEncoding[1] = 0;
	t2.nbTagRecords = 0;
	t2.sizeTagRecords = 0;


	//The first of this tag is probably the Device player name (found on NAW3000)
	//However is it displayed anywhere else than in sonicstage or connectplayer?...
	//probably not -> so we don't care ^-^

	for ( int i = 0; i < 64; i++ )
	{
		if ( ( i >= 4 ) && ( i != 33 ) )
		{
			if ( ! ( writeTrackHeader ( &t2, f ) ) )
			{
				LOG ( "WRITE ERROR 5\n" );
				return ( FAILED );
			}

			if ( ! ( writeTrackTag ( &tt2, "", f ) ) )
			{
				LOG ( "WRITE ERROR 6\n" );
				return ( FAILED );
			}
		}
		else
		{
			if ( i == 33 )
			{
				t1.fileType[3] = 0;
				t1.trackLength = 0;
				t1.trackEncoding = 0; //?? unknown value related to the album (this is not really trackEncoding)
			}

			if ( ! ( writeTrackHeader ( &t1, f ) ) )
			{
				LOG ( "WRITE ERROR 7\n" );
				return ( FAILED );
			}

			if ( ! ( writeTrackTag ( &tt1, "", f ) ) )
			{
				LOG ( "WRITE ERROR 8\n" );
				return ( FAILED );
			}
		}


	}
	fclose ( f );
	return ( SUCCESS );
}



ResultCode write_03GINFXX ( const char *path, list<Songlist*> *categories, int sortedBy )
{

	if ( path == NULL )
		return INVALID_PARAMETER;

	if ( ( categories == NULL ) || ( categories->size() <= 0 ) )
		return INVALID_PARAMETER_1;

	//write the 03GINFXX.DAT file
	FILE *f;
	int nbTags = 0;
	int nbCat = ( int ) categories->size();

	//open the file
	f = fopen ( path, "wb" );
	if ( f == NULL )
	{
		LOG ( "error can't open file" );
		LOG ( path );
		LOG ( "\n" );
		LOG ( "WRITE ERROR 9\n" );
		return FAILED;
	}

	if ( ( sortedBy == SORTED_ALBUM_WTAGS ) || ( sortedBy == SORTED_PLAYLIST ) )
		nbTags = 6;
	else
		nbTags = 1;

	//write the headers
	FileHeader header;
	ObjectPointer Opointer;
	ObjectHeader obj;

	memcpy ( header.magic, "GPIF", 4 );
	writeHeader ( &header, f );

	memcpy ( Opointer.magic, "GPFB", 4 );
	Opointer.length = UINT32_SWAP_BE_LE ( ( ( ( TAGSIZE * nbTags ) + 16 ) * nbCat ) +16 );//basically the size of the next block
	obj.size = UINT16_SWAP_BE_LE ( ( TAGSIZE * nbTags ) + 16 ); //(128 * nbtag) + tag header (same thing here)
	Opointer.offset = 0x20000000;
	writeObjectPointer ( &Opointer, f );

	memcpy ( obj.magic, "GPFB", 4 );
	obj.count = UINT16_SWAP_BE_LE ( nbCat );
	obj.padding[0] = 0;
	obj.padding[1] = 0;
	writeObject ( &obj, f );

	//write the tags
	//int nbWriten = 0;
	TrackHeader t;
	TrackTag tt;

	t.fileType[0] = 0;
	t.fileType[1] = 0;
	t.fileType[2] = 0;
	t.fileType[3] = 0;
	t.trackLength = 0; //fill song length, or list length (just before writing)
	t.trackEncoding = 0;
	t.nbTagRecords = UINT16_SWAP_BE_LE ( nbTags );
	t.sizeTagRecords = UINT16_SWAP_BE_LE ( TAGSIZE );

	//encoding
	tt.tagEncoding[0] = 0x00;
	tt.tagEncoding[1] = 0x02;

	for ( list<Songlist *>::iterator i = categories->begin(); i != categories->end(); i++ )
	{
		t.trackLength = UINT32_SWAP_BE_LE ( ( *i )->getLength() );

		if ( ! ( writeTrackHeader ( &t, f ) ) )
			return ( FAILED );

		//list of: album artist genre
		if ( sortedBy == SORTED_ALBUM_WTAGS )
		{
			Song *s = ( *i )->songs->front();

			//album tag
			memcpy ( tt.tagType, "TIT2", 4 );
			if ( ! ( writeTrackTag ( &tt, s->getAlbum().c_str(), f ) ) )
			{
				LOG ( "WRITE ERROR 10\n" );
				return ( FAILED );
			}

			//artist tag
			memcpy ( tt.tagType, "TPE1", 4 );
			if ( ! ( writeTrackTag ( &tt, s->getArtist().c_str(), f ) ) )
			{
				LOG ( "WRITE ERROR 11\n" );
				return ( FAILED );
			}

			//genre tag
			memcpy ( tt.tagType, "TCON", 4 );
			if ( ! ( writeTrackTag ( &tt, s->getGenre().c_str(), f ) ) )
			{
				LOG ( "WRITE ERROR 12\n" );
				return ( FAILED );
			}

			//tsop tag
			memcpy ( tt.tagType, "TSOP", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 13\n" );
				return ( FAILED );
			}

			//picp tag
			memcpy ( tt.tagType, "PICP", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 14\n" );
				return ( FAILED );
			}

			//pic0 tag
			memcpy ( tt.tagType, "PIC0", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 15\n" );
				return ( FAILED );
			}
		}
		else if ( sortedBy == SORTED_PLAYLIST )
		{
			//Playlist name
			memcpy ( tt.tagType, "TIT2", 4 );
			if ( ! ( writeTrackTag ( &tt, ( *i )->getName().c_str(), f ) ) )
			{
				LOG ( "WRITE ERROR 16\n" );
				return ( FAILED );
			}

			//empty artist tag
			memcpy ( tt.tagType, "TPE1", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 17\n" );
				return ( FAILED );
			}

			//empty genre tag
			memcpy ( tt.tagType, "TCON", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 18\n" );
				return ( FAILED );
			}

			//empty tsop tag
			memcpy ( tt.tagType, "TSOP", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 19\n" );
				return ( FAILED );
			}

			//empty picp tag
			memcpy ( tt.tagType, "PICP", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 20\n" );
				return ( FAILED );
			}

			//empty pic0 tag
			memcpy ( tt.tagType, "PIC0", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 21\n" );
				return ( FAILED );
			}
		}
		else
		{
			memcpy ( tt.tagType, "TIT2", 4 );
			if ( ! ( writeTrackTag ( &tt, ( *i )->getName().c_str(), f ) ) )
			{
				LOG ( "WRITE ERROR 22\n" );
				return ( FAILED );
			}
		}
	}

	fclose ( f );
	return ( SUCCESS );
}


//songs
ResultCode write_04CNTINF ( const char *path, list<Song *> *songs, bool isEncrypted )
{
	if ( path == NULL )
		return INVALID_PARAMETER;

	if ( ( songs == NULL ) || ( songs->size() <= 0 ) )
		return INVALID_PARAMETER_1;

	//write the 04CNTINF.DAT file
	FILE *f;
	//open the file
	f = fopen ( path, "wb" );
	if ( f == NULL )
	{
		LOG ( "error can't open file 04CNTINF.DAT\n" );
		LOG ( "WRITE ERROR 23\n" );
		return FAILED;
	}

	//write the headers
	FileHeader header;
	ObjectPointer Opointer;
	ObjectHeader obj;

	//get number of songs + empty songs
	int nbSlot = 0;
	for ( list<Song*>::iterator s = songs->begin(); s != songs->end(); s++ )
	{
		if ( ( *s )->getDbIndex() > nbSlot )
			nbSlot = ( *s )->getDbIndex();
	}

	memcpy ( header.magic, "CNIF", 4 );
	writeHeader ( &header, f );

	memcpy ( Opointer.magic, "CNFB", 4 );
	Opointer.length = UINT32_SWAP_BE_LE ( ( ( ( TAGSIZE * 5 ) + 16 ) * nbSlot ) +16 );
	Opointer.offset = 0x20000000;
	writeObjectPointer ( &Opointer, f );

	memcpy ( obj.magic, "CNFB", 4 );
	obj.count = UINT16_SWAP_BE_LE ( nbSlot );
	obj.size = UINT16_SWAP_BE_LE ( ( TAGSIZE * 5 ) + 16 ); //(128 * nbtag) + tag header
	obj.padding[0] = 0;
	obj.padding[1] = 0;
	writeObject ( &obj, f );

	//write the tags
	//int nbWriten = 0;
	TrackHeader t;
	TrackTag tt;

	t.fileType[0] = 0x00;
	t.fileType[1] = 0x00;
	t.fileType[2] = 0xff;

	//track is encrypted or not
	if ( isEncrypted )
	{
		t.fileType[3] = 0xfe;
	}
	else
	{
		t.fileType[3] = 0xff;
	}


	t.nbTagRecords = UINT16_SWAP_BE_LE ( 5 );
	t.sizeTagRecords = UINT16_SWAP_BE_LE ( TAGSIZE );

	//encoding
	tt.tagEncoding[0] = 0x00;
	tt.tagEncoding[1] = 0x02;

	int index = 1;
	for ( list<Song *>::iterator i = songs->begin(); i != songs->end(); i++ )
	{
		while ( ( ( *i )->getStatus() == Song::EMPTYTRACK ) || ( index != ( *i )->getDbIndex() ) )
		{
			//write empty song
			index++;

			if ( ! ( writeTrackHeader ( &t, f ) ) )
			{
				LOG ( "WRITE ERROR 24\n" );
				return ( FAILED );
			}

			memcpy ( tt.tagType, "TIT2", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 25\n" );
				return ( FAILED );
			}

			//artist tag
			memcpy ( tt.tagType, "TPE1", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 26\n" );
				return ( FAILED );
			}

			//album tag
			memcpy ( tt.tagType, "TALB", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 27\n" );
				return ( FAILED );
			}

			//genre tag
			memcpy ( tt.tagType, "TCON", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 28\n" );
				return ( FAILED );
			}

			//tsop tag
			memcpy ( tt.tagType, "TSOP", 4 );
			if ( ! ( writeTrackTag ( &tt, "", f ) ) )
			{
				LOG ( "WRITE ERROR 29\n" );
				return ( FAILED );
			}
		}

		if ( ( ( *i )->getStatus() == Song::EMPTYTRACK ) )
			continue;

		//next song
		index++;

		t.trackEncoding = UINT32_SWAP_BE_LE ( ( ( *i )->getEncoding() ) );
		t.trackLength = UINT32_SWAP_BE_LE ( ( *i )->getLength() );

		if ( ! ( writeTrackHeader ( &t, f ) ) )
		{
			LOG ( "WRITE ERROR 30\n" );
			return ( FAILED );
		}

		//title tag
		memcpy ( tt.tagType, "TIT2", 4 );
		if ( ! ( writeTrackTag ( &tt, ( *i )->getTitle().c_str(), f ) ) )
		{
			LOG ( "WRITE ERROR 31\n" );
			return ( FAILED );
		}

		//artist tag
		memcpy ( tt.tagType, "TPE1", 4 );
		if ( ! ( writeTrackTag ( &tt, ( *i )->getArtist().c_str(), f ) ) )
		{
			LOG ( "WRITE ERROR 32\n" );
			return ( FAILED );
		}

		//album tag
		memcpy ( tt.tagType, "TALB", 4 );
		if ( ! ( writeTrackTag ( &tt, ( *i )->getAlbum().c_str(), f ) ) )
		{
			LOG ( "WRITE ERROR 33\n" );
			return ( FAILED );
		}

		//genre tag
		memcpy ( tt.tagType, "TCON", 4 );
		if ( ! ( writeTrackTag ( &tt, ( *i )->getGenre().c_str(), f ) ) )
		{
			LOG ( "WRITE ERROR 34\n" );
			return ( FAILED );
		}

		//tsop tag
		memcpy ( tt.tagType, "TSOP", 4 );
		if ( ! ( writeTrackTag ( &tt, "", f ) ) )
		{
			LOG ( "WRITE ERROR 35\n" );
			return ( FAILED );
		}
	}
	fclose ( f );
	return ( SUCCESS );
}

ResultCode write_05CIDLST ( const char *path, list<Song *> *songs )
{
	if ( path == NULL )
		return INVALID_PARAMETER;

	if ( songs == NULL )
		return INVALID_PARAMETER_1;

	FILE *f;
	//int nbTags = 0;

	//open the file
	f = fopen ( path, "wb" );
	if ( f == NULL )
	{
		LOG ( "error can't open file" );
		LOG ( path );
		LOG ( "\n" );
		LOG ( "WRITE ERROR 36\n" );
		return FAILED;
	}

	//write the headers
	FileHeader header;
	ObjectPointer Opointer;
	ObjectHeader obj;

	memcpy ( header.magic, "CIDL", 4 );
	writeHeader ( &header, f );

	memcpy ( Opointer.magic, "CILB", 4 );
	Opointer.length = UINT32_SWAP_BE_LE ( ( ( 32 + 16 ) * songs->size() ) +16 );
	Opointer.offset = UINT32_SWAP_BE_LE ( 0x0020 );
	writeObjectPointer ( &Opointer, f );

	memcpy ( obj.magic, "CILB", 4 );
	obj.count = UINT16_SWAP_BE_LE ( songs->size() );
	obj.size = UINT16_SWAP_BE_LE ( 32 + 16 );
	obj.padding[0] = 0;
	obj.padding[1] = 0;
	writeObject ( &obj, f );

	uint8 t[16];
	uint8 tt[32];

	t[0] = 0; //WTF?
	t[1] = 0;
	t[2] = 0;
	t[3] = 0;
	t[4] = 0x01;
	t[5] = 0x0F;
	t[6] = 0x50;
	t[7] = 0x00;
	t[8] = 0x00;
	t[9] = 0x04;
	t[10] = 0;
	t[11] = 0;
	t[12] = 0;
	t[13] = 0x01; //fixme 01
	t[14] = 0x02; //fixme 02
	t[15] = 0x03; //fixme 03

	tt[0] = 0xc8; //value is different in NAW3000?!
	tt[1] = 0xd8;
	tt[2] = 0x36;
	tt[3] = 0xd8;

	for ( int i = 4; i < 32; i++ )
		tt[i] = 0;

	for ( list<Song *>::iterator song = songs->begin(); song != songs->end(); song++ )
	{
		if ( fwrite ( &t, 16, 1, f ) != 1 )
		{
			LOG ( "WRITE ERROR 37\n" );
			return ( FAILED );
		}
		tt[4] = 0x11; //fixme 11
		tt[5] = 0x22; //fixme 22
		tt[6] = 0x33; //fixme 33
		tt[7] = 0x44; //fixme 44
		if ( fwrite ( &tt, 32, 1, f ) != 1 )
		{
			LOG ( "WRITE ERROR 38\n" );
			return ( FAILED );
		}
	}

	fclose ( f );
	return ( SUCCESS );
}

//write empty playlist file
ResultCode write_03GINF22 ( const char *path )
{
	if ( path == NULL )
		return INVALID_PARAMETER;

	//write the 03GINF22.DAT file
	FILE *f;
	//int nbTags = 0;

	//open the file
	f = fopen ( path, "wb" );
	if ( f == NULL )
	{
		LOG ( "error can't open file" );
		LOG ( path );
		LOG ( "\n" );
		LOG ( "WRITE ERROR 39\n" );
		return FAILED;
	}

	//write the headers
	FileHeader header;
	ObjectPointer Opointer;
	ObjectHeader obj;

	memcpy ( header.magic, "GPIF", 4 );
	writeHeader ( &header, f );

	memcpy ( Opointer.magic, "GPFB", 4 );
	Opointer.length = UINT32_SWAP_BE_LE ( 16 );
	Opointer.offset = 0x20000000;
	writeObjectPointer ( &Opointer, f );

	memcpy ( obj.magic, "GPFB", 4 );
	obj.count = 0;//UINT16_SWAP_BE_LE(list.size());
	obj.size = UINT16_SWAP_BE_LE ( 784 ); //wtf?
	obj.padding[0] = 0;
	obj.padding[1] = 0;
	writeObject ( &obj, f );

	fclose ( f );
	return ( SUCCESS );
}

void updateDiskSpaceInfo ( char driveLetter, largeInteger *usedSpaceDisk, largeInteger *freeSpaceDisk, largeInteger *totalDiskSpaceValue )
{
#ifndef __GNUC__
	char drive[2];

	drive[0] = driveLetter;
	drive[1] = ':';
	drive[2] = 0;

	ULARGE_INTEGER free={0,};
	ULARGE_INTEGER total={0,};
	ULARGE_INTEGER freeb={0,};
	BOOL res = GetDiskFreeSpaceEx ( drive, &free, &total, &freeb );
	*usedSpaceDisk = total.QuadPart - freeb.QuadPart;
	*freeSpaceDisk = freeb.QuadPart;
	*totalDiskSpaceValue = total.QuadPart;
#endif
}

//without key
bool addOMA ( Song *s, string *fromPath )
{
	return ( addOMA ( s, fromPath, -1, NULL ) );
}

//with DvId.dat key
bool addOMA ( Song *s, string *fromPath, uint32 dvIdKey )
{
	int key = ( 0x2465 + s->getDbIndex() * 0x5296E435 ) ^ dvIdKey;
	return ( addOMA ( s, fromPath, key, NULL ) );
}

//with codeTable key
bool addOMA ( Song *s, string *fromPath, string *codeTablePath )
{
	//load decode table for this track
	int id = s->getDbIndex();

	FILE *t;
	uint8* codeTable = NULL;

	codeTable = ( uint8* ) malloc ( sizeof ( uint8 ) *1024 );
	uint8 *header1 = ( uint8* ) malloc ( sizeof ( uint8 ) * 134 ); //deserialize manually ...
	uint8 *header2 = ( uint8* ) malloc ( sizeof ( uint8 ) * 10 );
	uint8 *tmp = ( uint8* ) malloc ( sizeof ( uint8 ) * 256 );

	t = fopen ( codeTablePath->c_str(), "rb" );
	// place cursor in place...
	fseek ( t, 0x4B0 * id, SEEK_SET );
	fread ( header1, sizeof ( uint8 ), 134, t );

	for ( int i = 0; i < 4; i++ )
	{
		fread ( tmp, sizeof ( uint8 ), 256, t );
		for ( int j = 0; j < 256; j++ )
			codeTable[ ( i*256 ) + j] = tmp[j];
		fread ( header2, sizeof ( uint8 ), 10, t );
	}

	free ( header1 );
	free ( header2 );
	free ( tmp );
	fclose ( t );

	if ( codeTable == NULL )
		return false;

	bool res = addOMA ( s, fromPath, -1, codeTable );
	free ( codeTable );
	return res;
}


//without key
bool getOMA ( Song *s, string *toPath )
{
	return ( getOMA ( s, toPath, -1, NULL ) );
}

//with DvId.dat key
bool getOMA ( Song *s, string *toPath, uint32 dvIdKey )
{
	int key = ( 0x2465 + s->getDbIndex() * 0x5296E435 ) ^ dvIdKey;
	return ( getOMA ( s, toPath, key, NULL ) );
}

//with codeTable key
bool getOMA ( Song *s, string *toPath, string *codeTablePath )
{
	//load decode table for this track
	int id = s->getDbIndex();

	FILE *t;
	uint8* codeTable = NULL;

	codeTable = ( uint8* ) malloc ( sizeof ( uint8 ) *1024 );
	uint8 *header1 = ( uint8* ) malloc ( sizeof ( uint8 ) * 134 ); //deserialize manually ...
	uint8 *header2 = ( uint8* ) malloc ( sizeof ( uint8 ) * 10 );
	uint8 *tmp = ( uint8* ) malloc ( sizeof ( uint8 ) * 256 );

	t = fopen ( codeTablePath->c_str(), "rb" );
	// place cursor in place...
	fseek ( t, 0x4B0 * id, SEEK_SET );
	fread ( header1, sizeof ( uint8 ), 134, t );

	for ( int i = 0; i < 4; i++ )
	{
		fread ( tmp, sizeof ( uint8 ), 256, t );
		for ( int j = 0; j < 256; j++ )
			codeTable[ ( i*256 ) + j] = tmp[j];
		fread ( header2, sizeof ( uint8 ), 10, t );
	}

	free ( header1 );
	free ( header2 );
	free ( tmp );
	fclose ( t );

	if ( codeTable == NULL )
		return false;

	bool res = getOMA ( s, toPath, -1, codeTable );
	free ( codeTable );
	return res;
}

//add mp3 file to device
bool getOMA ( Song *s, string *toPath, uint32 key, uint8 *codeTable )
{
	if ( s->getStatus() != Song::PRESENT )
		return ( false );

	if ( toPath == NULL )
		return false;

	char	    filename[512];
	uint8	    *header = ( uint8* ) malloc ( sizeof ( uint8 ) * 11 );
	char	    *tmpTag = ( char* ) malloc ( sizeof ( char ) * 256 );
	int			tagLength;
	int			headerLength = 0;

	//open the file
	wsprintf ( filename, "%s\\%s - %s - %02i - %s.mp3",
	           toPath->c_str(),
	           s->getArtist().c_str(),
	           s->getAlbum().c_str(),
	           s->getTrack(),
	           s->getTitle().c_str() );

	LOG ( "getting file from " );
	LOG ( s->getFileName().c_str() );
	LOG ( " to " )
	LOG ( filename );
	LOG ( "\n" );

	FILE *fout = fopen ( filename, "wb" );
	if ( fout == NULL )
	{
		LOG ( "error can't open file :" )
		LOG ( filename );
		LOG ( "\n" );
		return false;
	}

	FILE *fin = fopen ( s->getFileName().c_str(), "rb" );
	if ( fin == NULL )
	{
		LOG ( "error can't open file :" )
		LOG ( filename );
		LOG ( "\n" );
		fclose ( fout );
		return false;
	}

	//reserve 10bytes for the header
	memset ( header, 0, 11 );
	fwrite ( header, sizeof ( uint8 ), 10, fout );

	//title tag
	memset ( header, 0, 11 );
	memcpy ( header, "TIT2", 4 );
	tagLength = ( int ) s->getTitle().length() + 1;
	header[4] = SYNCHSAFE_B1 ( tagLength );//size of the title
	header[5] = SYNCHSAFE_B2 ( tagLength );
	header[6] = SYNCHSAFE_B3 ( tagLength );
	header[7] = SYNCHSAFE_B4 ( tagLength );
	fwrite ( header, sizeof ( uint8 ), 11, fout );
	headerLength += ( 11 * sizeof ( uint8 ) );
	fwrite ( s->getTitle().c_str(), sizeof ( uint8 ), ( tagLength - 1 ), fout );
	headerLength += ( ( tagLength - 1 ) * sizeof ( uint8 ) );

	//artist tag
	memset ( header, 0, 11 );
	memcpy ( header, "TPE1", 4 );
	tagLength = ( int ) s->getArtist().length() + 1;
	header[4] = SYNCHSAFE_B1 ( tagLength );//size of the title
	header[5] = SYNCHSAFE_B2 ( tagLength );
	header[6] = SYNCHSAFE_B3 ( tagLength );
	header[7] = SYNCHSAFE_B4 ( tagLength );
	fwrite ( header, sizeof ( uint8 ), 11, fout );
	headerLength += ( 11 * sizeof ( uint8 ) );
	fwrite ( s->getArtist().c_str(), sizeof ( uint8 ), ( tagLength - 1 ), fout );
	headerLength += ( ( tagLength - 1 ) * sizeof ( uint8 ) );


	//album tag
	memset ( header, 0, 11 );
	memcpy ( header, "TALB", 4 );
	tagLength = ( int ) s->getAlbum().length() + 1;
	header[4] = SYNCHSAFE_B1 ( tagLength );//size of the title
	header[5] = SYNCHSAFE_B2 ( tagLength );
	header[6] = SYNCHSAFE_B3 ( tagLength );
	header[7] = SYNCHSAFE_B4 ( tagLength );
	fwrite ( header, sizeof ( uint8 ), 11, fout );
	headerLength += ( 11 * sizeof ( uint8 ) );
	fwrite ( s->getAlbum().c_str(), sizeof ( uint8 ), ( tagLength - 1 ), fout );
	headerLength += ( ( tagLength - 1 ) * sizeof ( uint8 ) );

	//track number tag
	wsprintf ( tmpTag, "%02i", s->getTrack() );
	memset ( header, 0, 11 );
	memcpy ( header, "TRCK", 4 );
	tagLength = ( int ) strlen ( tmpTag ) + 1;
	header[4] = SYNCHSAFE_B1 ( tagLength );//size of the title
	header[5] = SYNCHSAFE_B2 ( tagLength );
	header[6] = SYNCHSAFE_B3 ( tagLength );
	header[7] = SYNCHSAFE_B4 ( tagLength );
	fwrite ( header, sizeof ( uint8 ), 11, fout );
	headerLength += ( 11 * sizeof ( uint8 ) );
	fwrite ( tmpTag, sizeof ( uint8 ), ( tagLength - 1 ), fout );
	headerLength += ( ( tagLength - 1 ) * sizeof ( uint8 ) );

	//year tag
	if ( s->getYear() > 0 )
	{
		wsprintf ( tmpTag, "%i", s->getYear() );
		memset ( header, 0, 11 );
		memcpy ( header, "TYER", 4 );
		tagLength = ( int ) strlen ( tmpTag ) + 1;
		header[4] = SYNCHSAFE_B1 ( tagLength );//size of the title
		header[5] = SYNCHSAFE_B2 ( tagLength );
		header[6] = SYNCHSAFE_B3 ( tagLength );
		header[7] = SYNCHSAFE_B4 ( tagLength );
		fwrite ( header, sizeof ( uint8 ), 11, fout );
		headerLength += ( 11 * sizeof ( uint8 ) );
		fwrite ( tmpTag, sizeof ( uint8 ), ( tagLength - 1 ), fout );
		headerLength += ( ( tagLength - 1 ) * sizeof ( uint8 ) );
	}

	//genre tag
	memset ( header, 0, 11 );
	memcpy ( header, "TCON", 4 );
	tagLength = ( int ) s->getGenre().length() + 1;
	header[4] = SYNCHSAFE_B1 ( tagLength );//size of the title
	header[5] = SYNCHSAFE_B2 ( tagLength );
	header[6] = SYNCHSAFE_B3 ( tagLength );
	header[7] = SYNCHSAFE_B4 ( tagLength );
	fwrite ( header, sizeof ( uint8 ), 11, fout );
	headerLength += ( 11 * sizeof ( uint8 ) );
	fwrite ( s->getGenre().c_str(), sizeof ( uint8 ), ( tagLength - 1 ), fout );
	headerLength += ( ( tagLength - 1 ) * sizeof ( uint8 ) );

	memset ( header, 0, 11 );
	//fill the rest with 0
	while ( ( headerLength %16 ) != 0 )
	{
		headerLength += sizeof ( uint8 );
		fwrite ( header, sizeof ( uint8 ), 1, fout );
	}
	//return to the start of the file to write the header;
	fseek ( fout, 0, SEEK_SET );
	memset ( header, 0, 11 );
	header[0] = 'I';
	header[1] = 'D';
	header[2] = '3';
	header[3] = 0x03;
	//...zeros
	header[6] = SYNCHSAFE_B1 ( headerLength );
	header[7] = SYNCHSAFE_B2 ( headerLength );
	header[8] = SYNCHSAFE_B3 ( headerLength );
	header[9] = SYNCHSAFE_B4 ( headerLength );
	fwrite ( header, sizeof ( uint8 ), 10, fout );

	//return after the header
	fseek ( fout, headerLength, SEEK_CUR );

	free ( header );
	free ( tmpTag );

	//skip the oma tags
	fseek ( fin, 0xC60, SEEK_SET );

	int   BLOCK_SIZE = 32767;
	int   blockNumber = 1;
	uint8 *inputData = ( uint8 * ) malloc ( sizeof ( uint8 ) * BLOCK_SIZE );
	uint8 *outputData = ( uint8 * ) malloc ( sizeof ( uint8 ) * BLOCK_SIZE );
	int   nbRead;
	long  position = 0;

	// input file -> decode -> output file
	while ( ( nbRead = ( int ) fread ( inputData, 1, BLOCK_SIZE, fin ) ) != 0 )
	{
		for ( int i = 0; i < nbRead; i++ )
		{
			if ( ( key == -1 ) && ( codeTable == NULL ) ) //just copy without decoding
			{
				outputData[i] = inputData[i];
			}
			else if ( key != -1 ) //DvId.dat
			{
				if ( ( position % 4 ) == 0 ) outputData[i] = ( ( inputData[i] ) ^ ( ( key & 0xFF000000 ) >> 24 ) );
				if ( ( position % 4 ) == 1 ) outputData[i] = ( ( inputData[i] ) ^ ( ( key & 0x00FF0000 ) >> 16 ) );
				if ( ( position % 4 ) == 2 ) outputData[i] = ( ( inputData[i] ) ^ ( ( key & 0x0000FF00 ) >> 8 ) );
				if ( ( position % 4 ) == 3 ) outputData[i] = ( ( inputData[i] ) ^ ( key & 0x000000FF ) );
			}
			else if ( codeTable != NULL ) //keyEncodeTable.dat
			{
				outputData[i] = codeTable[ ( ( position % 4 ) * 256 ) + inputData[i]];
			}
			position++;
		}
		fwrite ( outputData, sizeof ( uint8 ), nbRead, fout );
		blockNumber++;
	}

	free ( inputData );
	free ( outputData );

	fclose ( fout );
	fclose ( fin );
	//wsprintf(tmp, "del %s\n", filename);
	//system(tmp);
	return ( true );
}


//do not use this one directly
bool addOMA ( Song *s, string *fromPath, uint32 key, uint8 *codeTable )
{
	if ( ( s == NULL ) || ( fromPath == NULL ) )
		return false;

	s->setStatus ( Song::UPLOADING );

	//source
	char *from = ( char* ) fromPath->c_str();
	FILE *fin;
	bool isVBR = false;

	//destination
	string destString = s->getFileName();
	const char *dest =  destString.c_str();
	FILE *fout;

	LOG ( "sending" );
	LOG ( from );
	LOG ( " To " );
	LOG ( dest );
	LOG ( "\n" );

	//open destination file
	fout = fopen ( dest, "wb" );
	if ( fout == NULL )
	{
		LOG ( "error can't open file :" )
		LOG ( dest );
		LOG ( "\n" );
		s->setStatus ( Song::FAILED );
		return false;
	}

	//open originating file
	fin = fopen ( from, "rb" );
	float finSize = 0;
	if ( fin == NULL )
	{
		LOG ( "error can't open file :" )
		LOG ( from );
		LOG ( "\n" );
		DeleteFile ( dest );
		fclose ( fout );
		s->setStatus ( Song::FAILED );
		return false;
	}

	//get input file size
	struct stat results;
	if ( stat ( from, &results ) == 0 )
		finSize = ( float ) results.st_size;

	uint8	    *header = ( uint8* ) malloc ( sizeof ( uint8 ) * 11 );
	utf16char	*tagRecord;
	char	    *tmpTag = ( char* ) malloc ( sizeof ( char ) * 256 );
	int			tagLength;
	int			headerLength = 0;
	long	    startPoint = 0;
	int			nbFrames = 0; // estimated number of frames

	memset ( header, 0, 11 );
	if ( fread ( header, 1, 10, fin ) != 10 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}

	if ( STRNCMP_NULLOK ( ( char* ) header, "ID3", 3 ) == 0 )
	{
		//skip Idv2 tags
		startPoint = ( ( header[6] << 21 ) + ( header[7] << 14 ) + ( header[8] << 7 ) + header[9] ) + 10;
	}
	else
	{
		//no idv2 tag found
		startPoint = 0;
	}

	//go to the first frame
	fseek ( fin, startPoint, SEEK_SET );

	memset ( header, 0, 11 );
	//skip zeros
	while ( header[0] == 0 )
	{
		if ( fread ( header, sizeof ( uint8 ), 1, fin ) != 1 )
		{
			DeleteFile ( dest );
			fclose ( fout );
			fclose ( fin );
			s->setStatus ( Song::FAILED );
			return false;
		}
	}

	if ( header[0] == 0xFF )
	{
		if ( fread ( header, sizeof ( uint8 ), 3, fin ) != 3 )
		{
			DeleteFile ( dest );
			fclose ( fout );
			fclose ( fin );
			s->setStatus ( Song::FAILED );
			return false;
		}

		//get mpeg type, layer type and bitrate
		if ( ( header[0] & 0xE0 ) == 0xE0 ) //we found the first frame
		{
			//encoding =  mpeg version(2bits), layer version(2bits), bitrate(4bits)
			s->setEncoding ( ( ( header[0] & 0x1E ) << 3 ) + ( ( header[1] & 0xF0 ) >> 4 ) );

			// 00 - MPEG Version 2.5 (unofficial extension of MPEG 2)
			// 01 - reserved
			// 10 - MPEG Version 2 (ISO/IEC 13818-3)
			// 11 - MPEG Version 1 (ISO/IEC 11172-3)
			uint8 mpegVersion = ( header[0] & 0x18 ) >> 3;

			//00 - reserved
			//01 - Layer III
			//10 - Layer II
			//11 - Layer I
			uint8 layerVersion = ( header[0] & 0x06 ) >> 1;

			uint8 samplingRateIndex = ( header[1] & 0xC ) >> 2;

			if ( ( ( mpegVersion * 3 ) + samplingRateIndex >= 12 ) || ( ( mpegVersion * 3 ) + layerVersion >= 16 ) )
			{
				//header is invalid
				nbFrames = 0;
			}
			else
			{
				int  SAMPLING_RATES[] = {11025, 12000, 8000, 0, 0, 0, 22050, 24000, 16000, 44100, 48000, 32000};
				int  SAMPLE_PER_FRAME[] = {0,576,1152,384,0,0,0,0,0,576,1152,384,0,1152,1152,384};

				//sample per frame 0=reserved
				//          MPG2.5 res        MPG2   MPG1
				//reserved  0      0          0      0
				//Layer III 576    0          576    1152
				//Layer II  1152   0          1152   1152
				//Layer I   384    0          384    384

				int samplingRate = SAMPLING_RATES[ ( mpegVersion * 3 ) + samplingRateIndex];
				int samplePerFrame = SAMPLE_PER_FRAME[ ( mpegVersion * 4 ) + layerVersion];
				nbFrames = ( s->getLength() * samplingRate ) / samplePerFrame;
			}

			//skip the the frame header
			fseek ( fin, sizeof ( uint8 ) * 32, SEEK_CUR );

			//check if first is "XING" for VBR files
			memset ( header, 0, 11 );
			if ( fread ( header, sizeof ( uint8 ), 4, fin ) != 4 )
			{
				fclose ( fout );
				fclose ( fin );
				s->setStatus ( Song::FAILED );
				return false;
			}

			if ( STRNCMP_NULLOK ( ( char* ) header, "XING", 4 ) == 0 )
				isVBR = true;
		}
		else
		{
			LOG ( "File format not recognized, could not find first frame%s\n" )
			LOG ( s->getFileName().c_str() );
			LOG ( "\n" );
			//return false;
		}
	}
	else
	{
		LOG ( "File format not recognized, could not find first frame%s\n" )
		LOG ( s->getFileName().c_str() );
		LOG ( "\n" );
		//return false;
	}



	//go back to the start of mp3 data
	fseek ( fin, startPoint, SEEK_SET );

	//write the OMA headers :
	//format is :
	//"ea3"0x03 (4bytes), sizeOfTotaltags (6bytes)
	//TIT2(4bytes) sizeOfTag(4bytes) 2flags (2bytes) 0x02(=utf16be format?) titleOftheSong
	//...
	//not all size are coded in synchsafe format


	//idv2 header
	memset ( header, 0, 11 );
	header[0] = 'e';
	header[1] = 'a';
	header[2] = '3';
	header[3] = 0x03;
	//...zeros
	header[8] = 0x17;
	header[9] = 0x76; //size of tag header in Synchsafe format (=3072 bytes - 10 of header)
	if ( fwrite ( header, sizeof ( uint8 ), 10, fout ) != 10 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}

	//title tag
	memset ( header, 0, 11 );
	memcpy ( header, "TIT2", 4 );
	tagLength = ( int ) s->getTitle().length();
	tagRecord = ansi_to_utf16 ( s->getTitle().c_str(), tagLength + 1, true );
	tagLength = ( tagLength * 2 ) + 1;
	header[4] = NOT_SYNCHSAFE_B1 ( tagLength );//size of the title
	header[5] = NOT_SYNCHSAFE_B2 ( tagLength );
	header[6] = NOT_SYNCHSAFE_B3 ( tagLength );
	header[7] = NOT_SYNCHSAFE_B4 ( tagLength );
	header[8] = 0;  //flag 1
	header[9] = 0;  //flag 2
	header[10] = 0x02;  //
	tagLength = ( tagLength - 1 ) / 2;
	if ( fwrite ( header, sizeof ( uint8 ), 11, fout ) != 11 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( 11 * sizeof ( uint8 ) );
	if ( fwrite ( tagRecord, sizeof ( utf16char ), tagLength, fout ) != tagLength )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( tagLength * sizeof ( utf16char ) );
	free ( tagRecord );


	//artist tag
	memset ( header, 0, 11 );
	memcpy ( header, "TPE1", 4 );
	tagLength = ( int ) s->getArtist().length();
	tagRecord = ansi_to_utf16 ( s->getArtist().c_str(), tagLength + 1, true );
	tagLength = ( tagLength * 2 ) + 1;
	header[4] = NOT_SYNCHSAFE_B1 ( tagLength );//size of the title
	header[5] = NOT_SYNCHSAFE_B2 ( tagLength );
	header[6] = NOT_SYNCHSAFE_B3 ( tagLength );
	header[7] = NOT_SYNCHSAFE_B4 ( tagLength );
	header[8] = 0;   //flag 1
	header[9] = 0;  //flag 2
	header[10] = 0x02;  //
	tagLength = ( tagLength - 1 ) / 2;
	if ( fwrite ( header, sizeof ( uint8 ), 11, fout ) != 11 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( 11 * sizeof ( uint8 ) );
	if ( fwrite ( tagRecord, sizeof ( utf16char ), tagLength, fout ) != tagLength )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( tagLength * sizeof ( utf16char ) );
	free ( tagRecord );

	//album tag
	memset ( header, 0, 11 );
	memcpy ( header, "TALB", 4 );
	tagLength = ( int ) s->getAlbum().length();
	tagRecord = ansi_to_utf16 ( s->getAlbum().c_str(), tagLength + 1, true );
	tagLength = ( tagLength * 2 ) + 1;
	header[4] = NOT_SYNCHSAFE_B1 ( tagLength );//size of the title
	header[5] = NOT_SYNCHSAFE_B2 ( tagLength );
	header[6] = NOT_SYNCHSAFE_B3 ( tagLength );
	header[7] = NOT_SYNCHSAFE_B4 ( tagLength );
	header[8] = 0;  //flag 1
	header[9] = 0;  //flag 2
	header[10] = 0x02;  //
	tagLength = ( tagLength - 1 ) / 2;
	if ( fwrite ( header, sizeof ( uint8 ), 11, fout ) != 11 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( 11 * sizeof ( uint8 ) );
	if ( fwrite ( tagRecord, sizeof ( utf16char ), tagLength, fout ) != tagLength )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( tagLength * sizeof ( utf16char ) );
	free ( tagRecord );

	//genre tag
	memset ( header, 0, 11 );
	memcpy ( header, "TCON", 4 );
	tagLength = ( int ) s->getGenre().length();
	tagRecord = ansi_to_utf16 ( s->getGenre().c_str(), tagLength + 1, true );
	tagLength = ( tagLength * 2 ) + 1;
	header[4] = NOT_SYNCHSAFE_B1 ( tagLength );//size of the title
	header[5] = NOT_SYNCHSAFE_B2 ( tagLength );
	header[6] = NOT_SYNCHSAFE_B3 ( tagLength );
	header[7] = NOT_SYNCHSAFE_B4 ( tagLength );
	header[8] = 0;  //flag 1
	header[9] = 0;  //flag 2
	header[10] = 0x02;  //
	tagLength = ( tagLength - 1 ) / 2;
	if ( fwrite ( header, sizeof ( uint8 ), 11, fout ) != 11 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( 11 * sizeof ( uint8 ) );
	if ( fwrite ( tagRecord, sizeof ( utf16char ), tagLength, fout ) != tagLength )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( tagLength * sizeof ( utf16char ) );
	free ( tagRecord );

	//track number tag
	wsprintf ( tmpTag, "OMG_TRACK %i", s->getTrack() );
	memset ( header, 0, 11 );
	memcpy ( header, "TXXX", 4 );
	tagLength = ( int ) strlen ( tmpTag );
	tagRecord = ansi_to_utf16 ( tmpTag, tagLength + 1, true );
	tagRecord[9] = 0; //space are replaced by 0x00 not 0x20
	tagLength = ( tagLength * 2 ) + 1;
	header[4] = NOT_SYNCHSAFE_B1 ( tagLength );//size of the title
	header[5] = NOT_SYNCHSAFE_B2 ( tagLength );
	header[6] = NOT_SYNCHSAFE_B3 ( tagLength );
	header[7] = NOT_SYNCHSAFE_B4 ( tagLength );
	header[8] = 0;   //flag 1
	header[9] = 0;  //flag 2
	header[10] = 0x02;  //
	tagLength = ( tagLength - 1 ) / 2;
	if ( fwrite ( header, sizeof ( uint8 ), 11, fout ) != 11 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( 11 * sizeof ( uint8 ) );
	if ( fwrite ( tagRecord, sizeof ( utf16char ), tagLength, fout ) != tagLength )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( tagLength * sizeof ( utf16char ) );
	free ( tagRecord );

	//Year tag
	wsprintf ( tmpTag, "OMG_TRLDA %04i/01/01 00:00:00", s->getYear() );
	memset ( header, 0, 11 );
	memcpy ( header, "TXXX", 4 );
	tagLength = ( int ) strlen ( tmpTag );
	tagRecord = ansi_to_utf16 ( tmpTag, tagLength + 1, true );
	tagRecord[9] = 0; //space are replaced by 0x00 not 0x20
	tagLength = ( tagLength * 2 ) + 1;
//	tagLength = (strlen(tmpTag) + 1) * 2; BUGGGG
	header[4] = NOT_SYNCHSAFE_B1 ( tagLength );//size of the title
	header[5] = NOT_SYNCHSAFE_B2 ( tagLength );
	header[6] = NOT_SYNCHSAFE_B3 ( tagLength );
	header[7] = NOT_SYNCHSAFE_B4 ( tagLength );
	header[8] = 0;   //flag 1
	header[9] = 0;  //flag 2
	header[10] = 0x02;  //
	tagLength = ( tagLength - 1 ) / 2;
	if ( fwrite ( header, sizeof ( uint8 ), 11, fout ) != 11 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( 11 * sizeof ( uint8 ) );
	if ( fwrite ( tagRecord, sizeof ( utf16char ), tagLength, fout ) != tagLength )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( tagLength * sizeof ( utf16char ) );
	free ( tagRecord );

	//track length tag
	wsprintf ( tmpTag, "%i", s->getLength() * 1000 );
	memset ( header, 0, 11 );
	memcpy ( header, "TLEN", 4 );
	tagLength = ( int ) strlen ( tmpTag );
	tagRecord = ansi_to_utf16 ( tmpTag, tagLength + 1, true );
	tagLength = ( tagLength * 2 ) + 1;
	header[4] = NOT_SYNCHSAFE_B1 ( tagLength );//size of the title
	header[5] = NOT_SYNCHSAFE_B2 ( tagLength );
	header[6] = NOT_SYNCHSAFE_B3 ( tagLength );
	header[7] = NOT_SYNCHSAFE_B4 ( tagLength );
	header[8] = 0;   //flag 1
	header[9] = 0;  //flag 2
	header[10] = 0x02;  //
	tagLength = ( tagLength - 1 ) / 2;
	if ( fwrite ( header, sizeof ( uint8 ), 11, fout ) != 11 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( 11 * sizeof ( uint8 ) );
	if ( fwrite ( tagRecord, sizeof ( utf16char ), tagLength, fout ) != tagLength )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( tagLength * sizeof ( utf16char ) );
	free ( tagRecord );

	memset ( header, 0, 11 );
	//fill the rest with 0
	while ( headerLength < 3062 )
	{
		headerLength += sizeof ( uint8 );
		if ( fwrite ( header, sizeof ( uint8 ), 1, fout ) != 1 )
		{
			DeleteFile ( dest );
			fclose ( fout );
			fclose ( fin );
			s->setStatus ( Song::FAILED );
			return false;
		}
	}

	//write second header fixme (some stuff are missing here... important?)
	uint8 *header2 = ( uint8* ) malloc ( sizeof ( uint8 ) * 16 );
	headerLength = 0;
	memset ( header2, 0, 16 );
	//first line
	memcpy ( header2, "EA3", 3 );
	header2[3] = 0x02;
	header2[4] = 0;   //size of 2nd header
	header2[5] = 0x60;//size of 2nd header
	header2[6] = 0xff;// + same value as in 05CIDLST.DAT
	if ( ( key == -1 ) && ( codeTable == NULL ) )
	{
		header2[7] = 0xff;// encoded not encoded?
	}
	else
	{
		header2[7] = 0xfe;// encoded not encoded?
	}
	header2[12] = 0x01;
	header2[13] = 0x0F;
	header2[14] = 0x50;
	header2[15] = 0x00;// - same value as in 05CIDLST.DAT
	if ( fwrite ( header2, sizeof ( uint8 ), 16, fout ) != 16 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( 16 * sizeof ( uint8 ) );

	//second line
	memset ( header2, 0, 16 );
	header2[1] = 0x04;// + same value as in 05CIDLST.DAT fixme
	//zeros...
	header2[5] = 0x01; //fixme 01
	header2[6] = 0x02; //fixme 02
	header2[7] = 0x03; //fixme 03
	header2[8] = 0xc8;
	header2[9] = 0xd8;
	header2[10] = 0x36;
	header2[11] = 0xd8;
	header2[12] = 0x11; //fixme 11
	header2[13] = 0x22; //fixme 22
	header2[14] = 0x33; //fixme 33
	header2[15] = 0x44; // - same value as in 05CIDLST.DAT
	if ( fwrite ( header2, sizeof ( uint8 ), 16, fout ) != 16 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( 16 * sizeof ( uint8 ) );

	//third line
	memset ( header2, 0, 16 );

	header2[0] = 0x03;// 3 = MP3
	header2[1] = ( isVBR ) ? 0x90 : 0x80 ;// VBR = 90, CBR = 80
	header2[2] = s->getEncoding();// mpeg version(2bits), layer version(2bits), bitrate(4bits)
	header2[3] = 0x10;//?? fixme

	//tracklength
	uint32 trackLengh = s->getLength() * 1000;
	header2[4] = ( uint8 ) ( ( ( trackLengh ) & ( uint32 ) 0xff000000U ) >> 24 );
	header2[5] = ( uint8 ) ( ( ( trackLengh ) & ( uint32 ) 0x00ff0000U ) >> 16 );
	header2[6] = ( uint8 ) ( ( ( trackLengh ) & ( uint32 ) 0x0000ff00U ) >>  8 );
	header2[7] = ( uint8 ) ( ( trackLengh ) & ( uint32 )  0x000000ffU );

	//number of frames
	header2[8] = ( uint8 ) ( ( ( nbFrames ) & ( uint32 ) 0xff000000U ) >> 24 );
	header2[9] = ( uint8 ) ( ( ( nbFrames ) & ( uint32 ) 0x00ff0000U ) >>  16 );
	header2[10] = ( uint8 ) ( ( ( nbFrames ) & ( uint32 ) 0x0000ff00U ) >>  8 );
	header2[11] = ( uint8 ) ( ( nbFrames ) & ( uint32 )  0x000000ffU );

	//padding
	header2[12] = 0x00;
	header2[13] = 0x00;
	header2[14] = 0x00;
	header2[15] = 0x00;
	if ( fwrite ( header2, sizeof ( uint8 ), 16, fout ) != 16 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	headerLength += ( 16 * sizeof ( uint8 ) );

	//padding
	memset ( header2, 0, 16 );
	if ( fwrite ( header2, sizeof ( uint8 ), 16, fout ) != 16 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	if ( fwrite ( header2, sizeof ( uint8 ), 16, fout ) != 16 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}
	if ( fwrite ( header2, sizeof ( uint8 ), 16, fout ) != 16 )
	{
		DeleteFile ( dest );
		fclose ( fout );
		fclose ( fin );
		s->setStatus ( Song::FAILED );
		return false;
	}

	free ( header2 );
	free ( header );
	free ( tmpTag );


	int   BLOCK_SIZE = 32767;
	int   blockNumber = 1;
	uint8 *inputData = ( uint8 * ) malloc ( sizeof ( uint8 ) * BLOCK_SIZE );
	uint8 *outputData = ( uint8 * ) malloc ( sizeof ( uint8 ) * BLOCK_SIZE );
	int   nbRead;
	long  position = 0;

	// input file -> decode -> output file
	while ( ( nbRead = ( int ) fread ( inputData, 1, BLOCK_SIZE, fin ) ) != 0 )
	{
		for ( int i = 0; i < nbRead; i++ )
		{
			if ( ( key == -1 ) && ( codeTable == NULL ) ) //just copy without decoding
			{
				outputData[i] = inputData[i];
			}
			else if ( key != -1 ) //DvId.dat
			{
				if ( ( position % 4 ) == 0 ) outputData[i] = ( ( inputData[i] ) ^ ( ( key & 0xFF000000 ) >> 24 ) );
				if ( ( position % 4 ) == 1 ) outputData[i] = ( ( inputData[i] ) ^ ( ( key & 0x00FF0000 ) >> 16 ) );
				if ( ( position % 4 ) == 2 ) outputData[i] = ( ( inputData[i] ) ^ ( ( key & 0x0000FF00 ) >> 8 ) );
				if ( ( position % 4 ) == 3 ) outputData[i] = ( ( inputData[i] ) ^ ( key & 0x000000FF ) );
			}
			else if ( codeTable != NULL ) //keyEncodeTable.dat
			{
				outputData[i] = codeTable[ ( ( position % 4 ) * 256 ) + inputData[i]];
			}


			position++;
		}
		if ( fwrite ( outputData, sizeof ( uint8 ), nbRead, fout ) != nbRead )
		{
			DeleteFile ( dest );
			fclose ( fout );
			fclose ( fin );
			s->setStatus ( Song::FAILED );
			return false;
		}
		blockNumber++;
		//totalByteLeftToWrite -= BLOCK_SIZE;

		//increment progress
		if ( ( finSize > 0 ) && ( BLOCK_SIZE > 0 ) )
			s->setProgress ( ( int ) ( s->getProgress() + 100 / ( finSize / BLOCK_SIZE ) ) );
	}
	s->setProgress ( 100 );


	free ( inputData );
	free ( outputData );

	fclose ( fout );
	fclose ( fin );
	s->setStatus ( Song::PRESENT );
	return ( true );
}



//********************** tools

//read FileHeader
bool getHeader ( FileHeader *header, FILE *f )
{
	//read header file
	if ( fread ( header, sizeof ( FileHeader ), 1, f ) != 1 )
	{
		LOG ( "error could not read file header (fileheader)\n" );
		return false;
	}
	return true;
}

//read an object pointer
bool getObjectPointer ( ObjectPointer *Opointer, FILE *f )
{
	//read object pointer
	if ( fread ( Opointer, sizeof ( ObjectPointer ), 1, f ) != 1 )
	{
		LOG ( "error could not read file header (filePointer)\n" );
		return false;
	}
	else
	{
		//get values from big endian
		Opointer->length = UINT32_SWAP_BE_LE ( Opointer->length );
		Opointer->offset = UINT32_SWAP_BE_LE ( Opointer->offset );
	}
	return true;
}

//read an object header
bool getObjectHeader ( ObjectHeader *obj, FILE *f )
{
	//read object
	if ( fread ( obj, sizeof ( ObjectHeader ), 1, f ) !=1 )
	{
		LOG ( "error could not read Object Header\n" );
		return false;
	}
	else
	{
		//get values from big endian
		obj->size = UINT16_SWAP_BE_LE ( obj->size );
		obj->count = UINT16_SWAP_BE_LE ( obj->count );
	}
	return true;
}

// write a file header
bool writeHeader ( FileHeader *h, FILE *f )
{
	return ( writeHeader ( h, f, 1 ) );
}

// write a file header specifying the object pointer count
bool writeHeader ( FileHeader *h, FILE *f, int count )
{
	h->cte[0] = 0x01;
	h->cte[1] = 0x01;
	h->cte[2] = 0x00;
	h->cte[3] = 0x00;
	h->count = count;
	for ( int i = 0; i < 7; i++ )
		h->padding[i]= 0x00;

	if ( fwrite ( h, sizeof ( FileHeader ), 1, f ) == 1 )
		return ( true );
	else
		return ( false );
}

// write an object pointer header
bool writeObjectPointer ( ObjectPointer *p, FILE *f )
{
	p->padding = 0x00000000;

	if ( fwrite ( p, sizeof ( FileHeader ), 1, f ) == 1 )
		return ( true );
	else
		return ( false );
	return ( true );
}

//internal write an object header
bool writeObject ( ObjectHeader *obj, FILE *f )
{
	if ( fwrite ( obj, sizeof ( FileHeader ), 1, f ) == 1 )
		return ( true );
	else
		return ( false );
}

//internal write a track header
bool writeTrackHeader ( TrackHeader *t, FILE *f )
{
	if ( fwrite ( t, sizeof ( TrackHeader ), 1, f ) != 1 )
	{
		LOG ( "error could not write file header (object)\n" );
		return false;
	}
	return ( true );
}

//internal write a track tag
bool writeTrackTag ( TrackTag *tt, const char *input, FILE *f )
{
	//init
	int size = TAGSIZE;
	utf16char *tagRecord;

	//to utf16
	tagRecord = ( utf16char* ) ansi_to_utf16 ( input, ( size - 6 ), true );

	//write tracktag (tagtype + encoding)
	if ( fwrite ( tt, sizeof ( TrackTag ), 1, f ) != 1 ) return ( false );

	//write tag (data itself)
	if ( fwrite ( tagRecord, ( size - 6 ), 1, f ) != 1 ) return ( false );
	free ( tagRecord );

	return ( true );
}

#ifdef __Win32__
utf16char *ansi_to_utf16 ( const char  *str, long len, bool endian )
{

	utf16char *dest= ( utf16char* ) malloc ( sizeof ( utf16char ) * len );

	for ( int j = 0; j < len; j++ )
		dest[j] = 0;

	if ( !str ) return dest; //Return an empty buffer of the size needed

	int num = MultiByteToWideChar ( CP_ACP,0,str,-1, ( LPWSTR ) dest, ( int ) strlen ( str ) +1 );

	//endianness
	if ( endian )
	{
		for ( int i = 0; i < len; i++ )
			dest[i] = UINT16_SWAP_BE_LE ( dest[i] );
	}
	return dest;
}

char *utf16_to_ansi ( const utf16char *str, long len, bool endian )
{
	if ( !str ) return NULL;
	if ( len > 2048 ) return NULL;
	char dest[2048]="";
	char * d=dest;

	utf16char *src = ( utf16char* ) malloc ( sizeof ( utf16char ) * len );

	for ( int j = 0; j < len; j++ )
	{
		if ( str[j] != 0 )
			src[j] = str[j];
		else
			src[j] = 0;
	}

	//endianness
	if ( endian )
	{
		for ( int i = 0; i < len; i++ )
			src[i] = UINT16_SWAP_BE_LE ( src[i] );
	}

	WideCharToMultiByte ( CP_ACP,0, ( LPCWSTR ) src,-1,d,sizeof ( dest )-1,NULL,NULL );
	dest[2047]=0;
	return _strdup ( dest );
}

#else

/// Do not forgot to free the returned buffer after used
utf16char *ansi_to_utf16 ( const char  *str, long len, bool endian )
{
	utf16char *dest= ( utf16char* ) malloc ( sizeof ( utf16char ) * len );

	memset ( dest, 0, len * 2 );
	if ( !str ) return dest; //Return an empty buffer of the needed size

	for ( int i=0; i < len; i++ )
	{
		if ( ! endian )
		{
			dest[i * 2]=0;
			dest[i * 2 + 1]=str[i];
		}
		else
		{
			dest[i * 2]=str[i];
			dest[i * 2 + 1]=0;
		}
	}

	return dest;
}

char *utf16_to_ansi ( const utf16char *str, long len, bool endian )
{
	if ( !str ) return NULL;
	if ( len > 2048 ) return NULL;

	char *dest= ( char* ) malloc ( len + 1 );
	memset ( dest, 0, len + 1 );

	if ( !str ) return dest;

	int j=0;
	for ( int i=0; j < len && i < len * 2; i++ )
	{
		if ( str[i] ) dest[j++]= str[i];
	}

	return dest;
}


#endif // __GNUC__
