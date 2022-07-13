// SonyDb.h
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

#if !defined(SONYDB_H)
#define SONYDB_H

#pragma once
#include <list>
#include <string>

#include "CommonDefines.h"
#include "NativeMethods.h"
#include "Song.h"
#include "Songlist.h"

using namespace std;

namespace SonyDb
{

	//just make things easier to read
	//a list of album is a list of songlist...
#define ALBUM		Songlist
#define ARTIST		Songlist
#define GENRE		Songlist
#define PLAYLIST	Songlist

	typedef enum
	{
		SENDING,
		PAUSED,
		FINISHED,
		DELETING,
		CANCELED
	} TaskStatus;

	typedef struct
	{
		TaskStatus status;
		int        progress; // 0 100%
		string    *statusDescription; //FIXME copying, encoding ...
		Song      *transferingSong;
		bool       sendingToDevice; //false=>downloading from device
	} Task;

	class Walkman
	{
		private:

			typedef enum
			{
				KEY_UNKNOWN,
				KEY_NONE,
				KEY_PROTECTED,
				KEY_TABLE
			} EncodingType;


			/**********/
			/* Values */
			/**********/

			//drive letter
			char driveLetter;

			//create a directory for the given song id
			void createDir ( int value );

			//device name
			string *deviceName;

			//all songs
			list<Song*>   *songs; //by DBindex
			list<Song*>   *songsByAlbums;
			list<Song*>   *songsByArtists;
			list<Song*>   *songsByGenres;

			//list of albums
			list<Songlist*> *albums;

			//list of artists
			list<Songlist*> *artists;

			//list of genres
			list<Songlist*> *genres;

			//list of playlist
			list<Songlist*> *playlists;


			//is the song, album, artist and genre list sorted?
			bool listAreSorted;

			//Free internal list (all except songs, see freeSongs)
			void deleteAllList();

			//Create and Sort all the list from list<Song*> songs
			bool sortAllList();

			//all task for this device
			list<Task*>	  *task;

			//encoding type : UNKNOWN, NONE, KEY, TABLE
			EncodingType keyType;
			unsigned int key;
			string *encodeTablePath;

			//Transfert speed (is calculated after first file has been transfered)
			int bytePerSec;

			//free disk space info
			largeInteger usedSpaceDisk;
			largeInteger freeSpaceDisk;
			largeInteger totalDiskSpaceValue;
			//Convert a largeInteger in something readable (Gb / Mb)...
			string *commaValue ( __int64 val0 );

			//free the list of songs (doesn't touch the player)
			ResultCode freeSongs();

			//activity status
			string status;

		public:
			Walkman ( char driveLetter );
			~Walkman();

			//write all DAT files necessary for the device
			ResultCode saveSonyDB();

			//write all DAT files to directory passed in argument
			ResultCode saveSonyDB ( string *path );

			//load the list of songs from the player database
			ResultCode loadSongs();

			//load the list of playlist from the player database
			ResultCode loadPlaylists();

			//free the list of playlists (doesn't touch the player)
			ResultCode freePlaylists();

			//Custom Database (saved on the device)
			//Load the devices info (song list, playlist, key etc...)
			ResultCode loadCustomDB();

			//Custom Database (saved on the device)
			//Load the devices info (song list, playlist, key etc...) from directory passed in argument
			ResultCode loadCustomDB ( string *path );

			//Save the device info (song list, playlist, keys etc...)
			ResultCode saveCustomDB();

			//Save the device info (song list, playlist, keys etc...) to directory passed in argument
			ResultCode saveCustomDB ( string *path );

			//try to get the keycode from mp3fm DvID.dat file
			ResultCode loadKeyCodeFromDvID ( string path );

			//try to get the table Keycode from GYM
			ResultCode loadKeyCodeFromTable ( string path );


			//get the OMA filename corresponding to the song id
			char *getOMAFilename ( int id );

			//set device name
			ResultCode setDeviceName ( string name );

			//set drive letter
			ResultCode setDriveLetter ( char c );

			//set key code
			ResultCode setKeyCode ( int keycode );

			//get drive letter
			char getDriveLetter();

			//get device name
			string *getDeviceName();

			//get the list of songs do not erase or alter those list yourself
			const list<Song*>   *getSongList();
			const list<Song*>   *getSongsByAlbums();
			const list<Song*>   *getSongsByArtists();
			const list<Song*>   *getSongsByGenres();

			//get the list of albums (delete this list & elements yourself)
			list<string*> *getAlbumList();

			//get the list of artists (delete this list & elements yourself)
			list<string*> *getArtistList();

			//get the list of genres (delete this list & elements yourself)
			list<string*> *getGenreList();

			//add a song to the list
			ResultCode	addSong ( Song *s );

			//delete a song from the list
			ResultCode	deleteSong ( int dbIndex );

			//add a playlist to this instance
			Songlist *addPlaylist ( string name );

			//get the list of playlist
			const list<Songlist*> *getPlaylist();

			//write the playlist to the device
			ResultCode writePlaylist();
			ResultCode writePlaylist ( string *rootPath );

			//delete a playlist
			ResultCode deletePlaylist ( int index );

			//get a song from the device and save it as an mp3
			ResultCode getSong ( int dbIndex, string *path );

			//get a pointer to the list of task
			list<Task*> *getTaskList();

			//flush all tasks (add, del from/to the player)
			ResultCode cancelAndClose();

			//get used Disk space
			__int64 getUsedDiskSpace();
			string *getUsedDiskSpaceAsString();

			//get total Disk space
			__int64 getTotalDiskSpace();
			string *getTotalDiskSpaceAsString();

			//get free Disk space (Estimation of current free space, counting add and del)
			__int64 getFreeDiskSpace();
			string *getFreeDiskSpaceAsString();

			//get a string describing current device status
			const string getStatus();

			ostream& printTo ( ostream& strm );

	};

	class Helper
	{
		public:
			//allocate and get the device list or refresh an existing device list
			list<Walkman*> *scanForDevice ( list<SonyDb::Walkman*> *oldList );

			//tool function to cut the songDescription
			static string getNextValue ( string *nameAndValue );

			//try to get the keycode by sending a usb request
			static int askKeyCodeFromUsb();

			//try to get the keycode by sending a usb request
			//optional savePath will save the keycode to a "DvID.dat" file
			static int askKeyCodeFromUsb ( string *savePath );

	};
}
#endif /* !SONYDB_H */
