// Songlist.h
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

#ifndef SONGLIST_H
#define SONGLIST_H

#include <string>
#include "Song.h"

using namespace std;

namespace SonyDb
{

	//this is used for :
	//albums
	//artists
	//genres
	//playlists
	//basically this is a list of songs, but it can also be used in a list of list
	//because it has index, name and length attributes

	// A SongList object is :
	//	 An index = Needed to order list of Songlist, for example you may have several playlist
	//              (for genre, artist, album this index is used to write the sonydb in file order)
	//
	//	 A name = Name of the playlist,
	//            or name of the genre,
	//            or name of the album,
	//            or name of the artist
	//
	//   A length = represent the total play length of the songs in that list
	//
	//   A list of Song objects = pointers to all songs of this playlist
	//                                                  of this genre
	//                                                  of this album
	//                                                  of this artist

	class Songlist
	{
		private:
			string      *name_;
			int			index_;
			int			length_;

		public:

			typedef enum
			{
				SORT_WALBUM,
				SORT_ARTIST,
				SORT_ALBUM,
				SORT_GENRE,
				SORT_PLAYLIST
			} SortedBy;

			Songlist();
			Songlist ( string name, int sortedBy );
			Songlist ( Song* s, int index, int sortedBy );
			~Songlist();

			bool setName ( string name );
			bool setLength ( int length );
			bool setIndex ( int index );
			SortedBy	sortedBy;
			string		getName();
			int			getIndex();
			int			getLength();
			list<Song*> *songs;

			//output the songlist info as a string
			string *asString();

			//create a songlist object from a correct string
			static bool songlistFromString ( string songlistDescription, Songlist *p, list<Song*> *allSongs );

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

			static bool sortByName ( Songlist *a, Songlist *b )
			{
				return ( STRCMP1 ( a->getName(), b->getName() ) < 0 );
			}

			static bool sortByIndex ( Songlist *a, Songlist *b )
			{
				return ( a->getIndex() < b->getIndex() );
			}
	};
}

#endif /* !SONGLIST_H */
