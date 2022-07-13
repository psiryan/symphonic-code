// Song.h
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

#if !defined(SONG_H)
#include "CommonDefines.h"
#define SONG_H

#include <string>

using namespace std;

#define SORTED_ALPHA         0
#define SORTED_ALBUM_WTAGS   1
#define SORTED_ARTIST        2
#define SORTED_ALBUM	     3
#define SORTED_GENRE	     4
#define SORTED_PLAYLIST      22


namespace SonyDb
{

	class Song
	{

		private:

			//file name on local drive ex: "c:\mp3\mymusic.mp3"
			string *fileName_;

			//Artist name
			string	*artist_;

			//Album name
			string	*album_;

			//Title of the song
			string	*title_;

			//Genre of the song
			string	*genre_;

			//Track number of the song
			int		track_;

			//Year of the song
			int		year_;

			//Length of the song in seconds
			int		length_;

			//Order in the sony database
			int		sonyDbOrder_;

			// Status of the song
			// Song::PRESENT 0 is present on player
			// Song::ADD_TO_DEVICE 1 is not present on player needs & to be added
			// Song::DEL_FR_DEVICE 2 is present & needs to be removed
			// Song::EMPTYTRACK 3
			// Song::UNKNOWN
			// Song::FAILED : failed last operation
			// Song::UPLOADING : currently uploading this song to the device
			// Song::DOWNLOADING : currently downloading this song to the device
			int		status_ ;

			//percentage of completion
			int		progress_;

			// mpeg version(2bits), layer version(2bits), bitrate(4bits)
			int		encoding_;

		public:
			Song();
			~Song();

			typedef enum
			{
				PRESENT,
				ADD_TO_DEVICE,
				DEL_FR_DEVICE,
				EMPTYTRACK,
				UNKNOWN,
				FAILED,
				UPLOADING,
				DOWNLOADING
			} TaskStatus;

			bool setFileName ( const string fileName );
			bool setArtist ( const string artist );
			bool setAlbum ( const string album );
			bool setTitle ( const string title );
			bool setGenre ( const string genre );
			bool setFileName ( const char *fileName );
			bool setArtist ( const char *artist );
			bool setAlbum ( const char *album );
			bool setTitle ( const char *title );
			bool setGenre ( const char *genre );
			bool setTrack ( int track );
			bool setYear ( int year );
			bool setLength ( int length );
			bool setLengthArtist ( int length );
			bool setLengthAlbum ( int length );
			bool setLengthGenre ( int length );
			bool setEncoding ( int encoding );
			bool setStatus ( int status );
			bool setProgress ( int newPercent );
			bool setDbIndex ( int index );

			string getFileName();
			string getArtist();
			string getAlbum();
			string getTitle();
			string getGenre();
			int getTrack();
			int getYear();
			int getLength();
			int getStatus();
			int getProgress();
			int getEncoding();
			int getDbIndex();

			bool isSameArtist ( Song* );
			bool isSameAlbum ( Song* );
			bool isSameGenre ( Song* );
			bool isSameTitle ( Song* );

			//output the song info as a string
			string *asString();

			//create a song object from a correct string
			static bool songFromString ( string songDescription, Song *s );


			/* sort method */
#define SKIP_THE_AND_WHITESPACE(x) { while (!isalnum(*x) && *x) x++; if (!_strnicmp(x,"the ",4)) x+=4; while (*x == ' ') x++; }

			//compare to char strings skipping whitespaces and "the"
			static int STRCMP1 ( const string a, const string b )
			{
				const char *pa = a.c_str();
				const char *pb = b.c_str();

				if ( !pa ) pa="";
				else SKIP_THE_AND_WHITESPACE ( pa )
					if ( !pb ) pb="";
					else SKIP_THE_AND_WHITESPACE ( pb )
						return _stricmp ( pa,pb );
			}

			static bool sortByDbIndex ( Song *a, Song *b )
			{
				return ( a->getDbIndex() < b->getDbIndex() );
			}

			static bool sortByTrackNumber ( Song *a, Song *b )
			{
				if ( a->getTrack() == b->getTrack() )
					return ( sortByDbIndex ( a, b ) );
				else
					return ( a->getTrack() < b->getTrack() );
			}

			static bool sortByAlbumName ( Song *a, Song *b )
			{
				int res =  STRCMP1 ( a->getAlbum(), b->getAlbum() );
				if ( res == 0 )
					return ( sortByTrackNumber ( a, b ) ); //same album order by track
				else
					return ( res < 0 );

			}

			static bool sortByArtistName ( Song *a, Song *b )
			{
				int res =  STRCMP1 ( a->getArtist(), b->getArtist() );
				if ( res == 0 )
				{
					return ( sortByTitleName ( a, b ) ); //same artist are ordered by titlename
					//return (sortByAlbumName(a, b)); //same artist order by album
				}
				else
					return ( res < 0 );
			}

			static bool sortByTitleName ( Song *a, Song *b )
			{
				int res =  STRCMP1 ( a->getTitle(), b->getTitle() );
				if ( res == 0 )
					return ( sortByTrackNumber ( a, b ) ); //same title order by track number
				else
					return ( res < 0 );
			}

			static bool sortByGenreName ( Song *a, Song *b )
			{
				int res =  STRCMP1 ( a->getGenre(), b->getGenre() );
				if ( res == 0 )
				{
					return ( sortByTitleName ( a, b ) ); //same artist are ordered by titlename
					//return (sortByAlbumName(a, b)); //same genre order by album
				}
				else
					return ( res < 0 );
			}

			//FIXME getRawData() for stream play
	};

}

#endif //SONG_H
