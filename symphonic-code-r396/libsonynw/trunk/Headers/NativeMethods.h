// NativeMethods.h
// Do not use those methods!
// See test example for more info
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

#if !defined(NATIVE_METHOD_H)
#define NATIVE_METHOD_H

#include <list>
#include <string>
#include <sys/stat.h>
#include "Song.h"
#include "Songlist.h"


using namespace std;
using namespace SonyDb;

#define SORTED_ALPHA         0
#define SORTED_ALBUM_WTAGS   1
#define SORTED_ARTIST        2
#define SORTED_ALBUM		 3
#define SORTED_GENRE		 4
#define SORTED_PLAYLIST     22

//swap an unsigned int from big endian to little endian
#define UINT32_SWAP_BE_LE(val) ((uint32) ( \
	(((uint32) (val) & (uint32) 0x000000ffU) << 24) | \
	(((uint32) (val) & (uint32) 0x0000ff00U) <<  8) | \
	(((uint32) (val) & (uint32) 0x00ff0000U) >>  8) | \
	(((uint32) (val) & (uint32) 0xff000000U) >> 24)))

//swap an unsigned short from big endian to little endian
#define UINT16_SWAP_BE_LE(val) ((uint16) ( \
	(((uint16) (val) & (uint16) 0x00ffU) << 8) | \
	(((uint16) (val) & (uint16) 0xff00U) >> 8)))

#define SYNCHSAFE_B1(val) (((uint32) (val) >> 21) & (uint32) 0x000007F)
#define SYNCHSAFE_B2(val) (((uint32) (val) >> 14) & (uint32) 0x000007F)
#define SYNCHSAFE_B3(val) (((uint32) (val) >> 7) & (uint32) 0x000007F)
#define SYNCHSAFE_B4(val) (((uint32) (val) & (uint32) 0x0000007F))

#define NOT_SYNCHSAFE_B1(val) (uint8) (((val) & (uint32) 0xff000000U) >> 24);
#define NOT_SYNCHSAFE_B2(val) (uint8) (((val) & (uint32) 0x00ff0000U) >> 16);
#define NOT_SYNCHSAFE_B3(val) (uint8) (((val) & (uint32) 0x0000ff00U) >>  8);
#define NOT_SYNCHSAFE_B4(val) (uint8) ((val) & (uint32)  0x000000ffU);

utf16char *ansi_to_utf16 ( const char  *str, long len, bool endian );
char *utf16_to_ansi ( const utf16char *str, long len, bool endian );

#define TAGSIZE 128
#define OUTPUT_TAGSIZE 128


typedef struct
{
	uint8 magic[4];      /* 4 bytes : "magic file descriptor" */
	uint8 cte[4];        /* 4 bytes : Constant value */
	uint8 count;         /* 1 byte  : Number of object pointers */
	uint8 padding[7];    /* 7 bytes : padding */
	/*16 bytes total */
} FileHeader;

typedef struct
{
	uint8  magic[4];      /* 4 bytes : magic (same as object) */
	uint32 offset;        /* 4 bytes : offset of the object (from the beginning)*/
	uint32 length;        /* 4 bytes : size of (ObjectHeader + Object) in bytes */
	uint32 padding;       /* 4 bytes : padding */
	/*16 bytes total */
} ObjectPointer;

typedef struct
{
	uint8 magic[4];       /* 4 bytes : magic (same as object pointer) */
	uint16 count;         /* 2 bytes : record count */
	uint16 size;		  /* 2 bytes : record size */
	uint32 padding[2];    /* 8 bytes : padding */
	/*16 bytes total */
} ObjectHeader;

typedef struct
{
	uint8  fileType[4];
	uint32 trackEncoding;
	uint32 trackLength;
	uint16 nbTagRecords;
	uint16 sizeTagRecords;
} TrackHeader;

typedef struct
{
	uint8 tagType[4];
	uint8 tagEncoding[2];
} TrackTag;


//disk space updater
void updateDiskSpaceInfo ( char driveLetter, largeInteger *usedSpaceDisk, largeInteger *freeSpaceDisk, largeInteger *totalDiskSpaceValue );

/* file readers */
//read all tracks from the device to a song list
ResultCode read_04CNTINF ( string *path, list<Song *> *songs );

//read all playlist from the device to a playlist list
ResultCode read03GINF22 ( string *path, list<Songlist *> *playlists );

//read song playlist correspondance from the device
ResultCode read01TREE22 ( string *path, list<Song*> *songsByDbIndex, list<Songlist *> *playlists );

//read the track number directly from the omg header
ResultCode getTrackInfo ( char *path, Song *s );

/* file writers */

//stay the same
ResultCode write_00GTRLST ( const char *path );

//song <-> categories correspondance
ResultCode write_01TREEXX ( const char *path, list<Song *> *songs, list<Songlist *> *categories, int sortedBy );

//stay the same (empty playlist)
ResultCode write_01TREE22 ( const char *path );

//stay the same
ResultCode write_02TREINF ( const char *path );

//categories
ResultCode write_03GINFXX ( const char *path, list<Songlist *> *categories, int sortedBy );

//stay the same (empty playlist)
ResultCode write_03GINF22 ( const char *path );

//songs
ResultCode write_04CNTINF ( const char *path, list<Song *> *songs, bool isEncrypted );

//song <-> sonicstage/cp correspondance?
ResultCode write_05CIDLST ( const char *path, list<Song *> *songs );

/* tools read*/
bool getHeader ( FileHeader *header, FILE *f );
bool getObjectPointer ( ObjectPointer *Opointer, FILE *f );
bool getObjectHeader ( ObjectHeader *obj, FILE *f );

/* tools write */
bool writeHeader ( FileHeader *h, FILE *f );
bool writeHeader ( FileHeader *h, FILE *f, int count );
bool writeObjectPointer ( ObjectPointer *p, FILE *f );
bool writeObject ( ObjectHeader *obj, FILE *f );
bool writeTrackTag ( TrackTag *tt, const char *input, FILE *f );
bool writeTrackHeader ( TrackHeader *t, FILE *f );

/* Get Oma file and convert it to mp3 */

//without key
bool getOMA ( Song *s, string *toPath );

//with DvId.dat key
bool getOMA ( Song *s, string *toPath, uint32 dvIdKey );

//with codeTable key
bool getOMA ( Song *s, string *toPath, string *codeTablePath );

//add mp3 file to device
bool getOMA ( Song *s, string *toPath, uint32 dvIdKey, uint8 *codeTable );


/* add Oma to device */

//without key
bool addOMA ( Song *s, string *fromPath );

//with DvId.dat key
bool addOMA ( Song *s, string *fromPath, uint32 dvIdKey );

//with codeTable key
bool addOMA ( Song *s, string *fromPath, string *codeTablePath );

//add mp3 file to device
bool addOMA ( Song *s, string *fromPath, uint32 dvIdKey, uint8 *codeTable );


/* transcode */
utf16char *ansi_to_utf16 ( const char  *str, long len, bool endian );
char *utf16_to_ansi ( const utf16char *str, long len, bool endian );


#endif /* !NATIVE_METHOD_H */

