// This is the main library file.
#include "SonyDb.h"

//drive system
#include <iostream>
#include <fstream>

using namespace SonyDb;


//Constructor
Walkman::Walkman ( char drive )
{
	/*#if DEBUG
	debugFile
	#endif*/
	LOGFUNCCALL ( ( "Walkman::Walkman()\n" ) );
	driveLetter = drive;
	deviceName = NULL;
	encodeTablePath = NULL;

	songs = NULL;
	songsByAlbums = NULL;
	songsByArtists = NULL;
	songsByGenres = NULL;
	albums = NULL;
	artists = NULL;
	genres = NULL;
	playlists = NULL;
	status = "Iddle";
	listAreSorted = false;

	key = 0xFFFFFFFF;
	keyType = Walkman::KEY_NONE;

	//Check if our own data file is present and load it
	if ( loadCustomDB() != SUCCESS )
	{
		//otherwise create Database from checking the OMA headers
		loadSongs();

		//load playlist
		loadPlaylists();

		//try to request the key code
		string pathRoot ( ":\\" );
		string path;
		pathRoot = driveLetter + pathRoot;

		path = pathRoot + "\\OMGAUDIO\\DvID.dat";
		if ( loadKeyCodeFromDvID ( path ) != SUCCESS )
		{
			path = pathRoot + "\\MP3FM\\DvID.dat";
			if ( loadKeyCodeFromDvID ( path ) != SUCCESS )
			{
				path = pathRoot + "\\GYM\\decodeKeys.dat";
				if ( loadKeyCodeFromTable ( path ) != SUCCESS )
				{
					LOG ( "No key code found\n" );
				}
			}
		}

	}

	if ( deviceName == NULL )
		deviceName = new string ( "Sony Walkman" );
}


//Destructor
Walkman::~Walkman()
{
	LOGFUNCCALL ( ( "Walkman::~Walkman()\n" ) );

	if ( deviceName != NULL )
		delete deviceName;

	freeSongs();
	freePlaylists();
	deleteAllList();
}

//set the keyCode
ResultCode Walkman::setKeyCode ( int keycode )
{
	if ( keycode <= 0 )
		return INVALID_PARAMETER;

	key = keycode;
	keyType = Walkman::KEY_PROTECTED;

	return SUCCESS;
}

//set device name
ResultCode Walkman::setDeviceName ( string name )
{
	LOGFUNCCALL ( ( "ResultCode Walkman::setDeviceName(string name)\n" ) );
	if ( name.size() <= 0 )
		return FAILED;

	if ( name.find ( DELIMITER_CHAR ) != string::npos )
		return FAILED;

	if ( deviceName != NULL ) delete deviceName;

	deviceName = new string ( name.c_str() );

	return SUCCESS;
}

//set drive letter
ResultCode Walkman::setDriveLetter ( char c )
{
	LOGFUNCCALL ( ( "ResultCode Walkman::setDriveLetter(char c)\n" ) );
	driveLetter = c;
	return SUCCESS;
}

//get drive letter
char Walkman::getDriveLetter()
{
	LOGFUNCCALL ( ( "char Walkman::getDriveLetter()\n" ) );
	return driveLetter;
}

//get device name
string *Walkman::getDeviceName()
{
	LOGFUNCCALL ( ( "string Walkman::getDeviceName()\n" ) );
	return deviceName;
}

//get the list of songs
const list<Song*> *Walkman::getSongList()
{
	LOGFUNCCALL ( ( "list<Song*> *Walkman::getSongList()\n" ) );
	return songs;
}

const list<Song*>   *Walkman::getSongsByAlbums()
{
	LOGFUNCCALL ( ( "list<Song*> *Walkman::getSongsByAlbums()\n" ) );
	if ( !listAreSorted )
		sortAllList();
	return songsByAlbums;
}

const list<Song*>   *Walkman::getSongsByArtists()
{
	LOGFUNCCALL ( ( "list<Song*> *Walkman::getSongsByArtists()\n" ) );
	if ( !listAreSorted )
		sortAllList();
	return songsByArtists;
}

const list<Song*>   *Walkman::getSongsByGenres()
{
	LOGFUNCCALL ( ( "list<Song*> *Walkman::getSongsByGenres()\n" ) );
	if ( !listAreSorted )
		sortAllList();
	return songsByGenres;
}

//list of albums
list<string*> *Walkman::getAlbumList()
{
	LOGFUNCCALL ( ( "list<Song*> *Walkman::getAlbumList()\n" ) );
	if ( !listAreSorted )
		sortAllList();

	if ( ( albums == NULL ) || ( albums->size() <= 0 ) )
		return NULL;

	list<string*> *albumsAsString = new list<string*>;

	for ( list<ALBUM*>::iterator album = albums->begin(); album != albums->end(); album++ )
	{
		albumsAsString->push_back ( new string ( ( *album )->getName() ) );
	}

	albumsAsString->sort();
	return albumsAsString;
}

//list of artists
list<string*> *Walkman::getArtistList()
{
	LOGFUNCCALL ( ( "list<Song*> *Walkman::getArtistList()\n" ) );
	if ( !listAreSorted )
		sortAllList();

	if ( ( artists == NULL ) || ( artists->size() <= 0 ) )
		return NULL;

	list<string*> *artistsAsString = new list<string*>;

	for ( list<ARTIST*>::iterator artist = artists->begin(); artist != artists->end(); artist++ )
	{
		artistsAsString->push_back ( new string ( ( *artist )->getName() ) );
	}
	artistsAsString->sort();
	return artistsAsString;
}

//list of genres
list<string*> *Walkman::getGenreList()
{
	LOGFUNCCALL ( ( "list<Song*> *Walkman::getGenreList()\n" ) );
	if ( !listAreSorted )
		sortAllList();

	if ( ( genres == NULL ) || ( genres->size() <= 0 ) )
		return NULL;

	list<string*> *genresAsString = new list<string*>;

	for ( list<GENRE*>::iterator genre = genres->begin(); genre != genres->end(); genre++ )
	{
		genresAsString->push_back ( new string ( ( *genre )->getName() ) );
	}

	genresAsString->sort();
	return genresAsString;
}

void Walkman::deleteAllList()
{
	//free all lists :
	if ( songsByAlbums != NULL )
	{
		songsByAlbums->clear();
		delete songsByAlbums;
	}

	if ( songsByArtists != NULL )
	{
		songsByArtists->clear();
		delete songsByArtists;
	}

	if ( songsByGenres != NULL )
	{
		songsByGenres->clear();
		delete songsByGenres;
	}

	if ( albums != NULL )
	{
		albums->clear();
		delete albums;
	}

	if ( artists != NULL )
	{
		artists->clear();
		delete artists;
	}

	if ( genres != NULL )
	{
		genres->clear();
		delete genres;
	}
}

//sort songs and create the list
bool Walkman::sortAllList()
{
	if ( songs == NULL )
		return true;

	if ( songs->size() <= 0 )
		return true;

	deleteAllList();

	if ( songs->size() >= 2 )
		songs->sort ( Song::sortByDbIndex ); //ordered by file number

	songsByAlbums = new list<Song*> ( *songs );
	songsByArtists = new list<Song*> ( *songs );
	songsByGenres = new list<Song*> ( *songs );

	if ( songsByAlbums->size() >= 2 )
		songsByAlbums->sort ( Song::sortByAlbumName );//ordered by album name and track number

	if ( songsByArtists->size() >= 2 )
		songsByArtists->sort ( Song::sortByArtistName );//ordered by artist name then title alpha

	if ( songsByGenres->size() >= 2 )
		songsByGenres->sort ( Song::sortByGenreName );//ordered by genre then title alpha

	albums = new list<ALBUM*>;
	artists = new list<ARTIST*>;
	genres = new list<GENRE*>;

	list<int> albums_length;
	list<int> artists_length;
	list<int> genres_length;

	//create the lists
	bool alreadyAddedArtist = false;
	bool alreadyAddedAlbum = false;
	bool alreadyAddedGenre = false;

	//each list is indexed in the file order
	int indexArtist = 1;
	int indexAlbum = 1;
	int indexGenre = 1;

	//build artist list
	for ( list<Song*>::iterator song = songs->begin(); song != songs->end(); song++ )
	{
		alreadyAddedArtist = false;
		alreadyAddedAlbum = false;
		alreadyAddedGenre = false;

		if ( ( *song )->getStatus() == Song::EMPTYTRACK )
			continue;

		//search in album list
		for ( list<ALBUM*>::iterator album = albums->begin(); album != albums->end(); album++ )
		{
			if ( ( *song )->isSameAlbum ( ( *album )->songs->front() ) )
			{
				alreadyAddedAlbum = true;

				//update the total length of this categorie of songs
				( *album )->setLength ( ( *album )->getLength() + ( *song )->getLength() );

				//add the song to this categorie
				( *album )->songs->push_back ( *song );
				break;
			}
		}

		//search in artist list
		for ( list<ARTIST*>::iterator artist = artists->begin(); artist != artists->end(); artist++ )
		{
			if ( ( *song )->isSameArtist ( ( *artist )->songs->front() ) )
			{
				alreadyAddedArtist = true;

				//update the total length of this categorie of songs
				( *artist )->setLength ( ( *artist )->getLength() + ( *song )->getLength() );

				//add the song to this categorie
				( *artist )->songs->push_back ( *song );
				break;
			}
		}

		//search in the genre list
		for ( list<GENRE*>::iterator genre = genres->begin(); genre != genres->end(); genre++ )
		{
			if ( ( *song )->isSameGenre ( ( *genre )->songs->front() ) )
			{
				alreadyAddedGenre = true;

				//update the total length of this categorie of songs
				( *genre )->setLength ( ( *genre )->getLength() + ( *song )->getLength() );

				//add the song to this categorie
				( *genre )->songs->push_back ( *song );
				break;
			}
		}

		//add it if not already in the list
		if ( !alreadyAddedGenre )
		{
			genres->push_back ( new GENRE ( *song, indexGenre++, Songlist::SORT_GENRE ) );
		}

		//add it if not already in the list
		if ( !alreadyAddedAlbum )
		{
			albums->push_back ( new ALBUM ( *song, indexAlbum++, Songlist::SORT_ALBUM ) );
		}

		//add it if not already in the list
		if ( !alreadyAddedArtist )
		{
			artists->push_back ( new ARTIST ( *song, indexArtist++, Songlist::SORT_ARTIST ) );
		}
	}

	/*
	//do not sort lists alphabetically, keep them in the file index order
	if (albums->size() >=2)
		albums->sort(Songlist::sortByName);

	if (artists->size() >= 2)
		artists->sort(Songlist::sortByName);

	if (genres->size() >= 2)
		genres->sort(Songlist::sortByName);
	*/

	listAreSorted = true;
	return true;
}


//get real file name from id, max is 255 files (from 01 to FF) per directory
char *Walkman::getOMAFilename ( int id )
{
	char *buffer = ( char* ) calloc ( sizeof ( char ), 256 );
	sprintf ( buffer, "%c:\\OMGAUDIO\\10F%02x\\1%07x.OMA", driveLetter, id >> 8, id );
	return ( buffer );
}


//write all DAT files necessary for the device
ResultCode Walkman::saveSonyDB()
{
	string path ( ":\\OMGAUDIO" );
	path = driveLetter + path;
	return saveSonyDB ( &path );
}

//write all DAT files necessary for the device
ResultCode Walkman::saveSonyDB ( string *rootPath )
{
	//FIXME ADD FAILED LOGS and what to do?
	LOGFUNCCALL ( ( "ResultCode Walkman::saveSonyDB()\n" ) );
	ResultCode res = SUCCESS;
	string path;

	sortAllList();
	path = *rootPath + "\\00GTRLST.DAT";
	if ( write_00GTRLST ( path.c_str() ) != SUCCESS )
	{
		res = FAILED;
		LOG ( "error writing file 00GTRLST.DAT\n" );
	}

	path = *rootPath + "\\02TREINF.DAT";
	if ( write_02TREINF ( path.c_str() ) != SUCCESS )
	{
		res = FAILED;
		LOG ( "error writing file 02TREINF.DAT\n" );
	}

	//write the categories in the file order
	path = *rootPath + "\\03GINF01.DAT";//albums here should be groups on E505
	if ( write_03GINFXX ( path.c_str(), albums, SORTED_ALBUM_WTAGS ) != SUCCESS )
	{
		res = FAILED;
		LOG ( "error writing file 03GINF01.DAT\n" );
	}

	//write groups by file order
	path = *rootPath + "\\01TREE01.DAT";
	if ( write_01TREEXX ( path.c_str(), songs, albums, SORTED_ALBUM_WTAGS ) != SUCCESS )
	{
		res = FAILED;
		LOG ( "error writing file 01TREE01.DAT\n" );
	}

	path = *rootPath + "\\03GINF02.DAT";
	if ( write_03GINFXX ( path.c_str(), artists, SORTED_ARTIST ) != SUCCESS )
	{
		res = FAILED;
		LOG ( "error writing file 03GINF02.DAT\n" );
	}

	path = *rootPath + "\\03GINF03.DAT";
	if ( write_03GINFXX ( path.c_str(), albums, SORTED_ALBUM ) != SUCCESS )
	{
		res = FAILED;
		LOG ( "error writing file 03GINF03.DAT\n" );
	}

	path = *rootPath + "\\03GINF04.DAT";
	if ( write_03GINFXX ( path.c_str(), genres, SORTED_GENRE ) != SUCCESS )
	{
		res = FAILED;
		LOG ( "error writing file 03GINF04.DAT\n" );
	}

	//Resort the categories alphabetically :
	if ( albums->size() >=2 )
		albums->sort ( Songlist::sortByName );

	if ( artists->size() >= 2 )
		artists->sort ( Songlist::sortByName );

	if ( genres->size() >= 2 )
		genres->sort ( Songlist::sortByName );

	//write those ordered alphabetically
	path = *rootPath + "\\01TREE02.DAT";
	if ( write_01TREEXX ( path.c_str(), songsByArtists, artists, SORTED_ARTIST ) != SUCCESS )
	{
		res = FAILED;
		LOG ( "error writing file 01TREE02.DAT\n" );
	}

	path = *rootPath + "\\01TREE03.DAT";
	if ( write_01TREEXX ( path.c_str(), songsByAlbums, albums, SORTED_ALBUM ) != SUCCESS )
	{
		res = FAILED;
		LOG ( "error writing file 01TREE03.DAT\n" );
	}

	path = *rootPath + "\\01TREE04.DAT";
	if ( write_01TREEXX ( path.c_str(), songsByGenres, genres, SORTED_GENRE ) != SUCCESS )
	{
		res = FAILED;
		LOG ( "error writing file 01TREE04.DAT\n" );
	}

	path = *rootPath + "\\04CNTINF.DAT";
	bool isEncoded = ( keyType == KEY_NONE ) ? false : true;
	if ( write_04CNTINF ( path.c_str(), songs, isEncoded ) != SUCCESS )
	{
		res = FAILED;
		LOG ( "error writing file 04CNTINF.DAT\n" );
	}

	path = *rootPath + "\\05CIDLST.DAT";
	if ( write_05CIDLST ( path.c_str(), songs ) != SUCCESS )
	{
		res = FAILED;
		LOG ( "error writing file 05CIDLST.DAT\n" );
	}

	return res;
}

//Load the devices info (song list, playlist, key etc...)
ResultCode Walkman::loadCustomDB()
{
	string path ( ":\\OMGAUDIO\\libsonynw.dat" );
	path = driveLetter + path;
	return loadCustomDB ( &path );
}

//Load the devices info (song list, playlist, key etc...) from directory passed as argument
ResultCode Walkman::loadCustomDB ( string *path )
{
	ResultCode res = SUCCESS;
	LOGFUNCCALL ( ( "ResultCode Walkman::loadCustomDB()\n" ) );

	ifstream file;

	file.open ( path->c_str() );

	if ( !file.is_open() )
		return FAILED;

	list<Song*> *tmpSongs = new list<Song *>;
	list<Songlist*> *tmpPlaylists = new list<Songlist*>;

	//VERSION
	string version;
	getline ( file, version );

	//DEVICE INFO
	string deviceInfo;
	getline ( file, deviceInfo );

	//get Descriptor
	size_t index = deviceInfo.find ( DELIMITER_CHAR );
	if ( index == string::npos )
		return FAILED;
	string selection = deviceInfo.substr ( 0, index );
	deviceInfo = deviceInfo.substr ( index + SIZE_DELIMITER_CHAR );
	if ( selection != "Device" )
		return FAILED;

	setDeviceName ( Helper::getNextValue ( &deviceInfo ) );

	int nbSongs = atoi ( Helper::getNextValue ( &deviceInfo ).c_str() );
	int nbPlaylist = atoi ( Helper::getNextValue ( &deviceInfo ).c_str() );

	key = atoi ( Helper::getNextValue ( &deviceInfo ).c_str() );
	keyType = ( EncodingType ) atoi ( Helper::getNextValue ( &deviceInfo ).c_str() );

	//SONGS
	string songAsString;
	Song *s;
	for ( int i = 0; i < nbSongs; i++ )
	{
		getline ( file, songAsString );
		s = new Song();
		if ( Song::songFromString ( songAsString, s ) )
		{
			s->setStatus ( Song::PRESENT );
			tmpSongs->push_back ( s );
		}
		else
		{
			delete s;
			res = FAILED;
			break;
		}
	}

	if ( res == SUCCESS )
	{
		string playlistAsString;
		Songlist *p;
		for ( int i = 0; i < nbPlaylist; i++ )
		{
			getline ( file, playlistAsString );
			p = new Songlist();
			if ( Songlist::songlistFromString ( playlistAsString, p, tmpSongs ) )
			{
				tmpPlaylists->push_back ( p );
			}
			else
			{
				delete p;
				res = FAILED;
				break;
			}
		}
	}

	file.close();

	//finally if success keep the new song list and playlists
	if ( res == SUCCESS )
	{
		freeSongs();
		songs = tmpSongs;
		freePlaylists();
		playlists = tmpPlaylists;
	}
	else
	{
		if ( tmpSongs != NULL )
		{
			if ( tmpSongs->size() > 0 )
			{
				for ( list<Song*>::iterator s = tmpSongs->begin(); s != tmpSongs->end(); s++ )
				{
					delete ( *s );
				}
			}
			tmpSongs->clear();
			delete tmpSongs;
		}
	}

	return SUCCESS;
}

ResultCode Walkman::saveCustomDB()
{
	string path ( ":\\OMGAUDIO" );
	path = driveLetter + path;
	return saveCustomDB ( &path );
}

//Save the device info (song list, playlist, keys etc...)
ResultCode Walkman::saveCustomDB ( string *path )
{
	LOGFUNCCALL ( ( "ResultCode Walkman::saveCustomDB()\n" ) );
	ofstream file;

	string path1 ( "\\libsonynw.dat" );
	path1 = *path + path1;

	file.open ( path1.c_str() );

	if ( !file.is_open() )
		return FAILED;

	//save version
	file << libsonynw_version << endl;

	//save device info
	this->printTo ( file );
	file.close();

	return SUCCESS;
}

//load the list of songs from the player database
ResultCode Walkman::loadSongs()
{
	LOGFUNCCALL ( ( "ResultCode Walkman::loadSongs()\n" ) );

	list<Song*> *tmpSongs = new list<Song *>;

	//Old way to get all the songs from 04CNTINF.DAT3
	/*string path;
	//int track;
	path += driveLetter;
	path += ":\\OMGAUDIO\\04CNTINF.DAT";
	ResultCode res = read_04CNTINF(&path, tmpSongs);

	if (res == SUCCESS)
	{
	if (tmpSongs->size() > 0)
	for (list<Song*>::iterator s = tmpSongs->begin(); s != tmpSongs->end(); s++)
	{
	char *filename = getOMAFilename((*s)->getDbIndex());
	(*s)->setFileName(filename);
	free(filename);
	//track = getTrackNumber((*s)->getFileName());
	//if (track > 0)
	//	(*s)->setTrack(track);
	}
	}*/

	char *directoryPath = ( char* ) calloc ( sizeof ( char ), 256 );
	sprintf ( directoryPath, "%c:\\OMGAUDIO\\10F00", driveLetter );
	QFileInfo dirinfo ( directoryPath ); //porting with Qt
	ResultCode res;

	//while a 10FXX directory exist scan it
	int j = 0;
	while ( dirinfo.exists() && dirinfo.isDir() ) //porting with Qt
	{
		//read every oma file tags
		for ( int i = 1; i <= 256; i++ )
		{
			char *filename = getOMAFilename ( i+ ( j*255 ) );
			QFileInfo fileinfo ( filename );	//porting with Qt
			if ( fileinfo.exists() )
			{
				Song *s = new Song();

				res = getTrackInfo ( filename, s );
				if ( res == SUCCESS )
				{
					s->setFileName ( filename );
					s->setStatus ( Song::PRESENT );
					s->setDbIndex ( j*255 + i );
					tmpSongs->push_back ( s );
				}
				else
				{
					delete s;
				}
				free ( filename );
			}
		}
		//change to next directory
		j++;
		sprintf ( directoryPath, "%c:\\OMGAUDIO\\10F%02x", driveLetter, ( j*256 ) >> 8 );
		QFileInfo dirinfo ( directoryPath ); //porting with Qt
	}
	free ( directoryPath );

	//finally if success keep the new song list
	if ( res == SUCCESS )
	{
		freeSongs();
		songs = tmpSongs;
	}
	else
	{
		if ( tmpSongs != NULL )
		{
			if ( tmpSongs->size() > 0 )
			{
				for ( list<Song*>::iterator s = tmpSongs->begin(); s != tmpSongs->end(); s++ )
				{
					delete ( *s );
				}
			}
			tmpSongs->clear();
			delete tmpSongs;
		}
	}

	return ( res );
}

//load the playlists
ResultCode Walkman::loadPlaylists()
{
	LOGFUNCCALL ( ( "ResultCode Walkman::loadPlaylist()\n" ) );
	string path ( ":\\OMGAUDIO\\" );
	string path1 = driveLetter + path + "03GINF22.DAT";
	string path2 = driveLetter + path + "01TREE22.DAT";
	list<Songlist*> *tmpPlaylists = new list<Songlist*>;

	//load the playlist names
	ResultCode res = read03GINF22 ( &path1, tmpPlaylists );

	if ( res == SUCCESS )
	{
		//load the songs for each playlist
		res = read01TREE22 ( &path2, songs, tmpPlaylists );
	}
	else
	{
		return res;
	}

	if ( res == SUCCESS )
	{
		if ( playlists != NULL )
		{
			playlists->clear();
			delete playlists;
			playlists = NULL;
		}
		playlists = tmpPlaylists;
		return SUCCESS;
	}

	return res;
}

//free the list of playlists (doesn't touch the player)
ResultCode Walkman::freePlaylists()
{
	LOGFUNCCALL ( ( "ResultCode Walkman::freePlaylists()\n" ) );
	if ( playlists != NULL )
	{
		if ( playlists->size() > 0 )
		{
			for ( list<Songlist *>::iterator p = playlists->begin(); p != playlists->end(); p++ )
			{
				delete ( *p );
			}
		}
		playlists->clear();
		delete playlists;
	}
	return FAILED;
}


//try to get the keycode from mp3fm DvID.dat file
ResultCode Walkman::loadKeyCodeFromDvID ( string path )
{
	int tmp;
	FILE *t = fopen ( path.c_str(), "rb" );
	if ( t != NULL )
	{
		fseek ( t, 0x0a, SEEK_SET );
		fread ( & ( tmp ), sizeof ( uint32 ), 1, t );
		tmp = UINT32_SWAP_BE_LE ( tmp );
		key = tmp;
		keyType = Walkman::KEY_PROTECTED;
		fclose ( t );
		return SUCCESS;
	}
	else
	{
		return FAILED;
	}
}

//try to get the table Keycode from GYM
ResultCode Walkman::loadKeyCodeFromTable ( string path )
{
	FILE *file = fopen ( path.c_str(), "rb" );
	if ( file != NULL )
	{
		encodeTablePath = new string ( path );
		key = -1;
		keyType = Walkman::KEY_TABLE;
		fclose ( file );
		return SUCCESS;
	}
	return FAILED;

}


//write the playlist to the device
ResultCode Walkman::writePlaylist()
{
	string path ( ":\\OMGAUDIO" );
	path = driveLetter + path;
	return writePlaylist ( &path );
}

//write the playlists to the device
ResultCode Walkman::writePlaylist ( string *rootPath )
{
	LOGFUNCCALL ( ( "ResultCode Walkman::writePlaylist()\n" ) );
	ResultCode res = SUCCESS;
	string path;

	if ( ( playlists == NULL ) || ( playlists->size() <= 0 ) )
	{
		//write empty playlist files :
		path = *rootPath + "\\01TREE22.DAT";
		if ( write_01TREE22 ( path.c_str() ) != SUCCESS )
		{
			res = FAILED;
		}

		path = *rootPath + "\\03GINF22.DAT";
		if ( write_03GINF22 ( path.c_str() ) != SUCCESS )
		{
			res = FAILED;
		}
	}
	else
	{
		//create a temporary list with all songs in the playlists
		list<Song*> allSongsInPlaylist;
		for ( list<Songlist*>::iterator pl = playlists->begin(); pl != playlists->end(); pl++ )
			for ( list<Song*>::iterator s = ( *pl )->songs->begin(); s != ( *pl )->songs->end(); s++ )
				allSongsInPlaylist.push_back ( *s );

		path = *rootPath + "\\01TREE22.DAT";
		if ( write_01TREEXX ( path.c_str(), &allSongsInPlaylist, playlists, SORTED_PLAYLIST ) != SUCCESS )
		{
			res = FAILED;
		}

		path = *rootPath + "\\03GINF22.DAT";
		if ( write_03GINFXX ( path.c_str(), playlists, SORTED_PLAYLIST ) != SUCCESS )
		{
			res = FAILED;
		}
	}

	return res;
}




//free the list of songs (doesn't touch the player)
ResultCode Walkman::freeSongs()
{
	LOGFUNCCALL ( ( "ResultCode Walkman::freeSongs()\n" ) );
	if ( songs != NULL )
	{
		if ( songs->size() > 0 )
		{
			for ( list<Song *>::iterator s = songs->begin(); s != songs->end(); s++ )
			{
				delete ( *s );
			}
		}
		songs->clear();
		delete songs;
	}
	return FAILED;
}

//create a directory given song id
void Walkman::createDir ( int value )
{
	char *dirName = ( char* ) calloc ( sizeof ( char ), 256 );
	sprintf ( dirName, "%c:\\OMGAUDIO\\10F%02x", driveLetter, value >> 8 );
	QDir lDir;
	lDir.mkdir ( dirName );//CreateDirectory(dirName, NULL); porting with Qt
	free ( dirName );
}

//add a song to the list (asynchrounsly send it to the player)
ResultCode  Walkman::addSong ( Song *s )
{
	LOGFUNCCALL ( ( "ResultCode  Walkman::addSong()\n" ) );
	//FIXME check if song is already present!

	//createTask

	//assign DbId and filename
	string *origine = new string ( s->getFileName() );

	//find the first free DbId FIXME lock this list!
	int dbId = -1;
	int prev = 0;
	int cur = 0;
	if ( songs == NULL )
	{
		songs = new list<Song *>;
	}
	else
	{
		if ( songs->size() >= 2 )
		{
			songs->sort ( Song::sortByDbIndex );
		}


		for ( list<Song*>::iterator songIt = songs->begin(); songIt != songs->end(); songIt++ )
		{
			cur = ( *songIt )->getDbIndex();
			if ( prev + 1 != cur )
			{
				dbId = prev + 1;
				break;
			}
			else
			{
				prev = cur;
			}
		}
	}

	//could not find any number just add it at the back
	if ( dbId == -1 )
		dbId = cur + 1;

	//add the song to the song list
	s->setDbIndex ( dbId );
	char *destination = getOMAFilename ( dbId );
	if ( destination != NULL )
		s->setFileName ( destination );
	else
		return FAILED;

	songs->push_back ( s );
	//LOCK ALL THIS

	//createDirectory if necessary
	createDir ( dbId );

	bool res = false;

	//Add the song to the player
	switch ( keyType )
	{
		case KEY_UNKNOWN :
		case KEY_NONE :
			res = addOMA ( s, origine );
			break;
		case KEY_PROTECTED :
			res = addOMA ( s, origine, key );
			break;
		case KEY_TABLE :
			res = addOMA ( s, origine, encodeTablePath );
			break;
	}

	//if fail free the DbId
	if ( res )
		return SUCCESS;
	else
		return FAILED;
}

//delete a song from the list (asynchrounsly delete it from the player)
ResultCode Walkman::deleteSong ( int dbIndex )
{
	LOGFUNCCALL ( ( "ResultCode Walkman::deleteSong(int index)\n" ) );

	for ( list<Song*>::iterator s = songs->begin(); s != songs->end(); s++ )
	{
		if ( ( *s )->getDbIndex() == dbIndex )
		{
			songs->erase ( s );
			DeleteFile ( ( *s )->getFileName().c_str() );
			listAreSorted = false;
			return SUCCESS;
		}
	}

	return FAILED;
}

//add a playlist to this instance (asynchrounsly send it to the player)
Songlist *Walkman::addPlaylist ( string name )
{
	LOGFUNCCALL ( ( "ResultCode  Walkman::addPlaylist()\n" ) );
	Songlist *p = new Songlist ( name, Songlist::SORT_PLAYLIST );
	int index = 0;
	if ( playlists == NULL )
	{
		playlists = new list<Songlist*>;
	}
	else
	{
		index = playlists->back()->getIndex() + 1;
	}
	p->setIndex ( index );
	playlists->push_back ( p );
	return p;
}

//get the list of playlist
const list<Songlist*> *Walkman::getPlaylist()
{
	LOGFUNCCALL ( ( "list<Songlist*> *Walkman::getPlaylist()\n" ) );
	if ( playlists == NULL )
		loadPlaylists();
	return playlists;
}

//delete a playlist (asynchrounsly delete it from the player)
ResultCode Walkman::deletePlaylist ( int index )
{
	LOGFUNCCALL ( ( "ResultCode Walkman::deletePlaylist(list<Songlist*>::iterator index)\n" ) );

	if ( index > ( int ) playlists->size() )
		return FAILED;

	Songlist *sl;
	list<Songlist*>::iterator p = playlists->begin();
	for ( int i = 0; i < index; i++ )
	{
		p++;
	}
	sl = *p;

	delete sl;
	playlists->erase ( p );
	return SUCCESS;
}

//get a song from the device and save it as an mp3
ResultCode Walkman::getSong ( int dBIndex, string *path )
{
	LOGFUNCCALL ( ( "ResultCode Walkman::getSong(list<Song*>::iterator index)\n" ) );

	Song *grabMe = NULL;

	for ( list<Song*>::iterator s = songs->begin(); s != songs->end(); s++ )
	{
		if ( ( *s )->getDbIndex() == dBIndex )
		{
			grabMe = *s;
			break;
		}
	}

	if ( grabMe == NULL )
		return FAILED;

	bool res = false;

	//Add the song to the player
	switch ( keyType )
	{
		case KEY_UNKNOWN :
		case KEY_NONE :
			res = getOMA ( grabMe, path );
			break;
		case KEY_PROTECTED :
			res = getOMA ( grabMe, path, key );
			break;
		case KEY_TABLE :
			res = getOMA ( grabMe, path, encodeTablePath );
			break;
	}

	if ( res )
		return SUCCESS;
	else
		return FAILED;
}

//get a pointer to the list of task
list<Task*>* Walkman::getTaskList()
{
	LOGFUNCCALL ( ( "list<Task*>* Walkman::getTaskList()\n" ) );
	//FIXME
	return NULL;
}



//cancel all unfinished task and write database
ResultCode Walkman::cancelAndClose()
{
	LOGFUNCCALL ( ( "ResultCode Walkman::cancelAndClose()\n" ) );
	//FIXME
	return FAILED;
}


// ********************************
//free disk space info
string *Walkman::commaValue ( __int64 val0 )
{
	char tmp[64];
	__int64 val1 = val0 / ( 1024*1024 );
	if ( ( val1>=1024 ) || ( val1 <= -1024 ) )
	{
		wsprintf ( tmp,"%d.%02d GB",val1/1024, ( val1%1024 ) /10 );
	}
	else
		wsprintf ( tmp,"%d MB",val1 );

	return new string ( tmp );
}

//get used Disk space
__int64 Walkman::getUsedDiskSpace()
{
	LOGFUNCCALL ( ( "int Walkman::getTotalDiskSpace()\n" ) );
	updateDiskSpaceInfo ( this->driveLetter, & ( this->usedSpaceDisk ), & ( this->freeSpaceDisk ), & ( this->totalDiskSpaceValue ) );
	return ( ( __int64 ) this->usedSpaceDisk );
}

string *Walkman::getUsedDiskSpaceAsString()
{
	LOGFUNCCALL ( ( "int Walkman::getTotalDiskSpace()\n" ) );
	updateDiskSpaceInfo ( this->driveLetter, & ( this->usedSpaceDisk ), & ( this->freeSpaceDisk ), & ( this->totalDiskSpaceValue ) );
	return ( commaValue ( this->usedSpaceDisk ) );
}

//get total Disk space
__int64 Walkman::getTotalDiskSpace()
{
	LOGFUNCCALL ( ( "int Walkman::getTotalDiskSpace()\n" ) );
	updateDiskSpaceInfo ( this->driveLetter, & ( this->usedSpaceDisk ), & ( this->freeSpaceDisk ), & ( this->totalDiskSpaceValue ) );
	return ( ( __int64 ) this->totalDiskSpaceValue );
}

string *Walkman::getTotalDiskSpaceAsString()
{
	LOGFUNCCALL ( ( "int Walkman::getTotalDiskSpace()\n" ) );
	updateDiskSpaceInfo ( this->driveLetter, & ( this->usedSpaceDisk ), & ( this->freeSpaceDisk ), & ( this->totalDiskSpaceValue ) );
	return ( commaValue ( this->totalDiskSpaceValue ) );
}


//get free Disk space (Estimation of current free space, counting add and del)
__int64 Walkman::getFreeDiskSpace()
{
	LOGFUNCCALL ( ( "int Walkman::getFreeDiskSpace()\n" ) );
	updateDiskSpaceInfo ( this->driveLetter, & ( this->usedSpaceDisk ), & ( this->freeSpaceDisk ), & ( this->totalDiskSpaceValue ) );
	return ( ( __int64 ) this->freeSpaceDisk );
}

string *Walkman::getFreeDiskSpaceAsString()
{
	LOGFUNCCALL ( ( "int Walkman::getFreeDiskSpace()\n" ) );
	updateDiskSpaceInfo ( this->driveLetter, & ( this->usedSpaceDisk ), & ( this->freeSpaceDisk ), & ( this->totalDiskSpaceValue ) );
	return ( commaValue ( this->freeSpaceDisk ) );
}


const string Walkman::getStatus()
{
	return ( status );
}

ostream&
Walkman::printTo ( std::ostream& strm )
{
	int nbSong = 0;
	int nbPlaylist = 0;

	if ( songs != NULL )
		nbSong = ( int ) songs->size();

	if ( playlists != NULL )
		nbPlaylist = ( int ) playlists->size();


	strm << "Device" << DELIMITER_CHAR << "deviceName=" << *getDeviceName() << DELIMITER_CHAR
	<< "nbSong=" << nbSong << DELIMITER_CHAR
	<< "nbPlaylist=" << nbPlaylist << DELIMITER_CHAR
	<< "key=" << key << DELIMITER_CHAR
	<< "keytype=" << keyType << DELIMITER_CHAR
	<< "\n";


	//save songs
	if ( ( songs != NULL ) && ( songs->size() > 0 ) )
	{
		for ( list<Song*>::iterator s = songs->begin(); s != songs->end(); s++ )
		{
			string *asString = ( *s )->asString();
			strm << asString->c_str() << endl;
			delete asString;
		}
		//std::copy(songs->begin(), songs->end(), std::ostream_iterator<Song*>(strm, "\n"));
	}

	//save playlists
	if ( ( playlists != NULL ) && ( playlists->size() > 0 ) )
	{
		for ( list<Songlist*>::iterator p = playlists->begin(); p != playlists->end(); p++ )
		{
			string *asString = ( *p )->asString();
			strm << asString->c_str() << endl;
			delete asString;
		}
		//std::copy(playlists->begin(), playlists->end(), std::ostream_iterator<Songlist*>(file, ""));
	}

	return strm;
}



// ********************************

string Helper::getNextValue ( string *nameAndValue )
{
	size_t index;
	string selection;

	if ( nameAndValue == NULL )
		return NULL;

	//get the first name=value
	index = nameAndValue->find ( DELIMITER_CHAR );
	selection = nameAndValue->substr ( 0, index );
	*nameAndValue = nameAndValue->substr ( index + SIZE_DELIMITER_CHAR );

	//get the value
	index = selection.find ( "=" );
	selection = selection.substr ( index+1 );
	return selection;
}


list<Walkman*>* Helper::scanForDevice ( list<Walkman*> *oldList )   //need to make it work in console mode...
{
	LOGFUNCCALL ( ( "list<Walkman*>* Helper::scanForDevice()\n" ) );

	//allocate a new list of device
	if ( oldList == NULL )
		oldList = new list<SonyDb::Walkman*>;

	QString dir = QFileDialog::getExistingDirectory ( Qwidget , tr ( "Chose the directory of your player" ),
	              "/media", QFileDialog::ShowDirsOnly | QFileDialog::DontResolveSymlinks );	
	oldList = dir;		// need convertion

	return ( oldList );
}



// int Helper::askKeyCodeFromUsb()
// {
// 	return (askKeyCodeFromUsb(NULL));
// }
//
// //Request keycode by sending a request by usb
// int Helper::askKeyCodeFromUsb(string *savePath)
// {
// 	LOGFUNCCALL(("ResultCode Helper::loadKeyCode()\n"));
// 	int tmpKey = -1;
//
// 	usb_init(); // initialize the library
// 	usb_find_busses(); // find all busses
// 	usb_find_devices(); // find all connected devices
//
// 	struct usb_bus *bus;
// 	struct usb_device *dev;
// 	int ret = 0;
// 	char tmp[256];
// 	usb_dev_handle *udev;
//
// 	unsigned char askKeyCode[] = { 0x55, 0x53, 0x42, 0x43, 0x11, 0x22, 0x33, 0x44, 0x12, 0x00, 0x00, 0x00, 0x80, 0x00, 0x0C, 0xA4, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xBC, 0x00, 0x12, 0x3F, 0x00, 0x00, 0x00, 0x00, 0x00 };
// 	unsigned char reset[] = { 0x55, 0x53, 0x42, 0x43, 0x11, 0x22, 0x33, 0x44, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
// 	unsigned char correctvalue[] = { 0x03 ,0x01 ,0x01 ,0x00 ,0x00 ,0x00 ,0x01 ,0x28 ,0x00 ,0x00 ,0x08 ,0xf6 ,0x29 ,0xe4 ,0x00 ,0x00 ,0x0d ,0x0a};
//
// 	for(bus = usb_get_busses(); bus; bus = bus->next)
// 	{
// 		for(dev = bus->devices; dev; dev = dev->next)
// 		{
// 			/*				LOG("VID:")
// 			LOG(dev->descriptor.idVendor);
// 			LOG("\n");
// 			LOG("PID:")
// 			LOG(dev->descriptor.idProduct);
// 			LOG("\n");
// 			LOG("manufacturer:");
// 			LOG(dev->descriptor.iManufacturer);
// 			LOG("\n");
// 			LOG("product:")
// 			LOG(dev->descriptor.iProduct);
// 			LOG("\n");
// 			*/
// 			if (dev->descriptor.idVendor == 1356) //sony VID=054c
// 			{
// 				udev = usb_open(dev);
// 				if (udev)
// 				{
// 					//Device Manufacturer
// 					if (dev->descriptor.iManufacturer) {
// 						ret = usb_get_string_simple(udev, dev->descriptor.iManufacturer, tmp, sizeof(tmp));
// 						if (ret > 0)
// 						{
// 							LOG("- Manufacturer : ");
// 							LOG(tmp);
// 							LOG("\n");
// 							//deviceName = new string(tmp);
// 						}
// 					}
//
// 					//device name
// 					if (dev->descriptor.iProduct) {
// 						ret = usb_get_string_simple(udev, dev->descriptor.iProduct, tmp, sizeof(tmp));
// 						if (ret > 0)
// 						{
// 							LOG("- Product      : ");
// 							LOG(tmp);
// 							LOG("\n");
// 							/*if (deviceName == NULL)
// 							deviceName = new string(tmp);
// 							else
// 							*deviceName = *deviceName + ":" + tmp;*/
// 						}
//
// 					}
//
// 					//End points :
// 					int EP_IN  = -1;
// 					int EP_OUT = -1;
// 					usb_interface_descriptor myInterface = dev->config[0].interface[0].altsetting[0];
//
// 					int nbEndPoint = myInterface.bNumEndpoints;
//
// 					if (nbEndPoint != 2)
// 					{
// 						LOG("Error: invalid number of end poing");
// 						usb_close(udev);
// 						break;
// 					}
//
// 					//first endpoint
// 					int addr = myInterface.endpoint[0].bEndpointAddress;
// 					if (!!(addr & USB_ENDPOINT_IN))
// 					{
// 						EP_IN = addr;
// 					}
// 					else
// 					{
// 						EP_OUT = addr;
// 					}
//
// 					//Second endpoint
// 					addr = myInterface.endpoint[1].bEndpointAddress;
// 					if (!!(addr & USB_ENDPOINT_IN))
// 					{
// 						EP_IN = addr;
// 					}
// 					else
// 					{
// 						EP_OUT = addr;
// 					}
//
// 					if ((EP_IN == -1) || (EP_OUT == -1))
// 					{
// 						LOG("Error: 2 input endpoint or 2 output endpoint");
// 						usb_close(udev);
// 						break;
// 					}
//
//
// 					if(usb_set_configuration(udev, 1) < 0)
// 					{
// 						LOG("error: setting config 1 failed\n");
// 						usb_close(udev);
// 						break;
// 					}
//
// 					if(usb_claim_interface(udev, 0) < 0)
// 					{
// 						LOG("error: claiming interface 0 failed\n");
// 						usb_close(udev);
// 						break;
// 					}
//
// 					memset(tmp, 0, 256);
// 					int nbWrite;
// 					int nbRead;
//
// 					//send a reset command
// 					nbWrite = usb_bulk_write(udev, EP_OUT, (char*)reset, sizeof(reset), 5000);
// 					nbRead = usb_bulk_read(udev, EP_IN, tmp, sizeof(tmp), 5000);
//
// 					//request Key code
// 					nbWrite = usb_bulk_write(udev, EP_OUT, (char*)askKeyCode, sizeof(askKeyCode), 5000);
// 					nbRead = usb_bulk_read(udev, EP_IN, tmp, sizeof(tmp), 5000);
//
//
// 					usb_release_interface(udev, 0);
// 					usb_close(udev);
//
// 					if (nbRead == 18)
// 					{
// 						LOG("Success : ");
// 						LOG(tmp);
// 						LOG("\n");
// 						tmpKey = (((tmp[12] << 24) & (uint32) 0xff000000U) +
// 							((tmp[13] << 16) & (uint32) 0x00ff0000U) +
// 							((tmp[14] << 8) & (uint32) 0x0000ff00U) +
// 							((tmp[15]) & (uint32) 0x000000ffU));
//
// 						//save this key as DvId.dat
// 						if (savePath != NULL)
// 						{
// 							FILE *fout = NULL;
// 							for (int i = 0; (i < 10) && (fout == NULL); i++)
// 							{
// 								fout = fopen(savePath->c_str(), "w");
// 								Sleep(2000);
// 							}
// 							if (fout == NULL)
// 							{
// 								LOG( "error can't open file" );
// 								LOG( savePath->c_str() );
// 								LOG( "\n" );
// 							}
// 							else
// 							{
// 								char* tmp2 = tmp + 2;
// 								if (fwrite(tmp2, sizeof(uint8), 16, fout) != 16)
// 								{
// 									LOG( "error can't save to file" );
// 									LOG( savePath->c_str() );
// 									LOG( "\n" );
// 								}
// 								fclose(fout);
// 							}
// 						}
//
// 					}
// 				}
// 			}
// 		}
// 	}
// 	return tmpKey;
// }
