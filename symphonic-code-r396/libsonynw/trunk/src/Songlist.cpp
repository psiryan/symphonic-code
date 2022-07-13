// Songlist.cpp
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

#include <string>
#include <iostream>
#include <sstream>
#include "Song.h"
#include "SonyDb.h"
#include "CommonDefines.h"
#include "Songlist.h"


using namespace std;
using namespace SonyDb;

Songlist::Songlist()
{
	sortedBy = ( SortedBy ) 2;
	name_ = NULL;
	index_ = 0;
	length_ =0;
	songs = new list<Song*>;
}

Songlist::Songlist ( string name, int sort )
{
	sortedBy = ( SortedBy ) sort;
	name_ = new string ( name );
	index_ = 0;
	length_ =0;
	songs = new list<Song*>;
}

Songlist::Songlist ( Song* s, int index, int sort )
{
	sortedBy = ( SortedBy ) sort;
	if ( sortedBy == SORT_PLAYLIST )
	{
		//first song is just the playlist descriptor
		name_ = new string ( s->getTitle() );
		index_ = index;
		length_ =0;
		songs = new list<Song*>;
	}
	else
	{
		name_ = NULL;
		index_ = index;
		length_ = s->getLength();
		songs = new list<Song*>;
		songs->push_back ( s );

		if ( sortedBy == SORT_ARTIST )
			name_ = new string ( s->getArtist() );

		if ( sortedBy == SORT_ALBUM )
			name_ = new string ( s->getAlbum() );

		if ( sortedBy == SORT_GENRE )
			name_ = new string ( s->getGenre() );
	}
}

Songlist::~Songlist()
{
	if ( name_ != NULL )
		delete name_;

	if ( songs != NULL )
	{
		songs->clear();
		delete songs;
	}
}

bool Songlist::setName ( string name )
{
	if ( name.size() <= 0 )
		return false;

	if ( name.find ( DELIMITER_CHAR ) != string::npos )
		return false;

	if ( name_ != NULL ) delete name_;

	name_ = new string ( name.c_str() );
	return true;
}

bool Songlist::setIndex ( int index )
{
	index_ = index;
	return true;
}

bool Songlist::setLength ( int length )
{
	length_ = length;
	return true;
}

string Songlist::getName()
{
	return *name_;
}

int	Songlist::getIndex()
{
	return index_;
}

int	Songlist::getLength()
{
	if ( ( length_ == 0 ) && ( songs != NULL ) && ( songs->size() > 0 ) )
	{
		for ( list<Song*>::iterator s = songs->begin(); s != songs->end(); s++ )
		{
			length_ += ( *s )->getLength();
		}
	}
	return length_;
}

string *Songlist::asString()
{
	ostringstream oss;
	int nbSongs = 0;

	if ( ( songs != NULL ) && ( songs->size() > 0 ) )
		nbSongs = ( int ) songs->size();


	oss <<  "Playlist" << DELIMITER_CHAR << "Index=" << getIndex() << DELIMITER_CHAR
	<< "name=" << getName() << DELIMITER_CHAR
	<< "nbSongs=" << nbSongs << DELIMITER_CHAR;

	for ( list<Song*>::iterator s = songs->begin(); s != songs->end(); s++ )
	{
		oss << ( *s )->getDbIndex() << DELIMITER_CHAR;
	}

	string *res = new string ( oss.str() );
	return res;
}

bool Songlist::songlistFromString ( string songlistDescription, Songlist *p, list<Song*> *allSongs )
{
	size_t index;
	string selection;

	if ( ( allSongs == NULL ) || ( p == NULL ) )
		return false;

	//get Descriptor
	index = songlistDescription.find ( DELIMITER_CHAR );
	if ( index == string::npos )
		return false;
	selection = songlistDescription.substr ( 0, index );
	songlistDescription = songlistDescription.substr ( index + SIZE_DELIMITER_CHAR );
	if ( selection != "Playlist" )
		return false;

	int i = 0;

	i = atoi ( Helper::getNextValue ( &songlistDescription ).c_str() );
	p->setIndex ( i );

	p->sortedBy = SORT_PLAYLIST;

	p->setName ( Helper::getNextValue ( &songlistDescription ) );

	i = atoi ( Helper::getNextValue ( &songlistDescription ).c_str() );

	//read all songs index and add them to the playlist
	int songIndex;
	for ( int j = 0; j < i; j++ )
	{
		songIndex = atoi ( Helper::getNextValue ( &songlistDescription ).c_str() );

		for ( list<Song*>::iterator s = allSongs->begin(); s != allSongs->end(); s++ )
		{
			if ( ( *s )->getDbIndex() == songIndex )
			{
				p->songs->push_back ( ( *s ) );
				p->setLength ( p->getLength() + ( *s )->getLength() );
				break;
			}
		}
	}

	return true;
}
