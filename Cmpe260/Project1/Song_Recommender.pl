% irem zeynep alagoz

% artist(ArtistName, Genres, AlbumIds).
% album(AlbumId, AlbumName, ArtistNames, TrackIds).
% track(TrackId, TrackName, ArtistNames, AlbumName, [Explicit, Danceability, Energy,
%                                                    Key, Loudness, Mode, Speechiness,
%                                                    Acousticness, Instrumentalness, Liveness,
%                                                    Valence, Tempo, DurationMs, TimeSignature]).


%Filter
features([explicit-0, danceability-1, energy-1,
          key-0, loudness-0, mode-1, speechiness-1,
       	  acousticness-1, instrumentalness-1,
          liveness-1, valence-1, tempo-0, duration_ms-0,
          time_signature-0]).

filter_features(Features, Filtered) :- 
    features(X), filter_features_rec(Features, X, Filtered).
filter_features_rec([], [], []).
filter_features_rec([FeatHead|FeatTail], [Head|Tail], FilteredFeatures) :-
    filter_features_rec(FeatTail, Tail, FilteredTail),
    _-Use = Head,
    (
        (Use is 1, FilteredFeatures = [FeatHead|FilteredTail]);
        (Use is 0,
            FilteredFeatures = FilteredTail
        )
    ).


%append_lists(+List1, +List2, -List3): Appends two lists.
append_lists([Head | List1], List2, [Head | List3]):-
	append_lists(List1, List2, List3).
append_lists([], List, List).

% count(+List, -Y): Counts the number of elements in a list
count([_|Tail],N) :- 
    count(Tail,Y), N is Y+1.
count([],0).

%membership(+X, +Y): Checks whether X is member of Y.
membership(X, [X|_]).
membership(X, [_|Y]) :- membership(X,Y).

%create_disjoint_lists(+List1, +List2, -Disjoint_List): Creates Disjoint_List by removing elements of List2 from List1.
create_disjoint_lists([Head|Tail], List2, Disjoint_List) :- 
        create_disjoint_lists(Tail, List2, Disjoint_List_Temp),
        ((\+ membership(Head, List2), 
        append_lists([Head], Disjoint_List_Temp, Disjoint_List),!); (membership(Head, List2), append_lists([], Disjoint_List_Temp, Disjoint_List))).
create_disjoint_lists([],_,[]).

%list_substring(+List1, +List2): Decides whether any element of List1 is a substring of any element of List2.
list_substring([Head|List1], List2) :-
    list_substring(List1, List2);
    check_list_substring(Head, List2),!.

%check_list_substring(+Substring, +List): Decides whether given element is a substring of any element of list. Helper for list_substring.
check_list_substring(Substring, [Head|List]) :-
    check_list_substring(Substring, List);
    substring(Substring, Head),!.

%substring(+X, +Y): Decides whether given element x is a substring of given element y. Helper for check_list_substring.
substring(X, Y) :-
     atom_length(X, L), sub_string(Y, _,L,_,S), X = S,!.

%columnN(+List, +N, -Column): Gets Nth column of given list of lists.
columnN([H|T], N, [R|Column]) :-
   rowN(H, N, R), columnN(T,N,Column).
columnN([],_,[]).
rowN([_|T],N,R) :-
    N1 is N-1, rowN(T,N1,R).
rowN([H|_],1,H):-!.

%get_average(+List, -Average_List): Gets average of lists.
get_average(List, Average_List) :-
    recursive_call(List, Sum_List), count(List, Size), division(Sum_List, Size, Average_List).

%recursive_call(+List, -Sum_List):Recursively calls sum_lists method to get sum of every list in the given list
recursive_call([Head|Tail], Sum_List) :- 
    recursive_call(Tail, Temp), 
    sum_lists(Head, Temp, Sum_List).
recursive_call([], []).

%sum_lists(+List1, +List2, -Sum_List): Calculates sum of two lists.
sum_lists([Head1|Tail1], [Head2| Tail2], [Sum_Element|Sum_List]) :- 
	sum_lists(Tail1, Tail2, Sum_List), 
    Sum_Element is Head1 + Head2.
sum_lists([], [], []).
sum_lists(List, [], List).

%division(+List, +Number, -Quotient_List): Divides every element of List by Number, puts quotients in new list named Quotient_List.
division([Head|List], Number, [Quotient|Quotient_List]) :-
    division(List, Number, Quotient_List), 
    Quotient is Head/Number.
division([],_,[]).

%list_distance(+List1, +List2, -Score): Calculates distance between two lists.
list_distance(List1, List2, Score) :-
    list_distance_square(List1, List2, Sum), Score is sqrt(Sum).

%list_distance_square(+List1, +List2, -Temp): Recursively calculates square of distance between two lists. Helper for list_distance.
list_distance_square([Head1|List1], [Head2| List2], Sum) :- 
	list_distance_square(List1, List2, Temp), 
    Difference_Element is Head1 - Head2,
    Square_Difference_Element is Difference_Element*Difference_Element,
    Sum is Square_Difference_Element + Temp.
list_distance_square([], [], 0).

%calculate_distance_tracks(+Id, -Distance_List): Calculates distance between given track and other tracks in tracks file. Puts values in Distance_List.
calculate_distance_tracks(Id, Distance_List) :- findall(
        [Distance, Track_Id, Track_Name],
        (track(Track_Id,Track_Name,_,_,_), Id \= Track_Id, trackDistance(Id, Track_Id, Distance)),
        Distance_List
    ).

%calculate_distance_albums(+Id, -Distance_List): Calculates distance between given album and other albums in albums file. Puts values in Distance_List.
calculate_distance_albums(Id, Distance_List) :- findall(
        [Distance, Album_Id, Album_Name],
        (album(Album_Id,Album_Name,_,_), Id \= Album_Id, albumDistance(Id, Album_Id, Distance)),
        Distance_List
    ).

%calculate_distance_artists(+Id, -Distance_List): Calculates distance between given artist and other artists in artists file.
calculate_distance_artists(Id, Distance_List) :- findall(
        [Distance, Artist_Name],
        (artist(Artist_Name,_,_), Id \= Artist_Name, artistDistance(Id, Artist_Name, Distance)),
        Distance_List
    ).

%calculate_distance_features(+Features1, -Distance_List): Calculates distance between given feature and other tracks' features. Helper for discoverPlaylist
calculate_distance_features(Features1, Track_List, Distance_List) :- findall(
        Distance-Track_Id-Track_Name-Artist_Name,
        (membership(Track_Id, Track_List), get_track_name(Track_Id, Track_Name), get_artist_names(Track_Id, Artist_Name), get_track_features(Track_Id, Features2), list_distance(Features1, Features2, Distance)),
        Distance_List
    ).

%get_top_n(+Distance_List, -Top_Distance_List): Gets top 30 element of given list(Ascending order).   
get_top_n(Distance_List, Top_Distance_List) :-
    sort(Distance_List, Sorted_List),
    rec_get_top_n(Sorted_List, 0 , Top_Distance_List).
%rec_get_top_n(+List, +N, -Top_List): Recursively gets top 30 element of the given sorted list. Helper for get_top_n method.
rec_get_top_n([Head|List], N, [Element|Top_List]) :-    
    N @< 30,
    N1 is N+1, 
    rec_get_top_n(List, N1, Top_List),
    Element = Head.      
rec_get_top_n(_, 30, []).
rec_get_top_n([],_, []).

%seperate_dashes(+Parent, -Child1, -Child2, -Child3, -Child4): Seperates a given list into 4 different lists based on dashes between eleements.
seperate_dashes([Head|Parent], [Head1|Child1], [Head2|Child2], [Head3|Child3], [Head4|Child4]) :- 
    seperate_dashes(Parent, Child1, Child2, Child3, Child4),
    Head1 - Head2 - Head3 - Head4 = Head.
seperate_dashes([],[],[],[],[]).

%get_artist_names(+Track_Id, -Artist_Names): Gets artist name of the given track id.
get_artist_names(Track_Id, Artist_Names) :- 
    track(Track_Id, _, Artist_Names, _, _).

%get_artist-genres(+Artist_Name, -Artist_Genres): Gets genres of given artist name.
get_artist_genres(Artist_Name, Artist_Genres) :-
    artist(Artist_Name, Artist_Genres, _ ).

%get_album_ids(+Artist_Name, -Album_Id): Gets album ids of given artist name.
get_album_ids(Artist_Name, Album_Id) :- 
    artist(Artist_Name, _, Album_Id).

%get_track_ids(+Album, -Track_Id): Gets track ids of given album id. 
get_track_ids(Album_Id, Track_Id) :- 
    album(Album_Id,_, _, Track_Id).

%get_track_name(+Track_Id, -Track_Name): Gets track name of given track id.
get_track_name(Track_Id, Track_Name) :- 
    track(Track_Id, Track_Name, _, _, _).

%get_track_features(+Track_Id, -Filtered_Track_Features): Gets filtered features of given track.
get_track_features(Track_Id, Filtered_Track_Features) :- 
    track(Track_Id, _, _, _, Track_Features), filter_features(Track_Features, Filtered_Track_Features).

%get_album_tracks_features(+Album_Id, -Filtered_Tracks_Features): Gets filtered features of given album's track.
get_album_tracks_features(Album_Id, Filtered_Tracks_Features) :- findall(
    Filtered_Track_Features,
    (get_track_ids(Album_Id, Tracks_Ids), membership(Track_Id, Tracks_Ids), get_track_features(Track_Id, Filtered_Track_Features)),
    Filtered_Tracks_Features
).

%get_artist_tracks_features(+Artist_Name, -Filtered_Tracks_Features): Gets filtered features of given artist's tracks.
get_artist_tracks_features(Artist_Name, Filtered_Tracks_Features) :- get_tracks_id_List(Artist_Name, Tracks_Ids), 
    findall(
        Filtered_Track_Features,
        (membership(Track_Id, Tracks_Ids), get_track_features(Track_Id, Filtered_Track_Features)),
        Filtered_Tracks_Features
    ).

%get_tracks_id_List(+Artist_Name, -Tracks_Ids): Gets tracks ids of given artist name. Helper for getArtistTracks.
get_tracks_id_List(Artist_Name, Tracks_Ids) :- findall(
    Id,
    (get_album_ids(Artist_Name, Albums), membership(Album, Albums), get_track_ids(Album, IdList), membership(Id, IdList), track(Id,_, _, _, _)),
    Tracks_Ids_List
), list_to_set(Tracks_Ids_List, Tracks_Ids).

%get_tracks_name_List(+Artist_Name, -Tracks_Names): Gets track names of given artist name. Helper for getArtistTracks.
get_tracks_name_List(Artist_Name, Tracks_Names) :- findall(
    Name,
    (get_album_ids(Artist_Name, Albums), membership(Album, Albums), get_track_ids(Album, Id_List), membership(Id, Id_List), get_track_name(Id, Name)),
    Tracks_Names
).

%suggested_playlist(+LikedGenres, +DislikedGenres, -Track_List): Creates a suggested list based on liked and disliked genres. Helper for discoverPlaylist.
suggested_playlist(LikedGenres, DislikedGenres, Track_List) :- findall(
        Track_Id,
        (track(Track_Id, _, _, _, _), 
        getTrackGenre(Track_Id, Genres), 
        list_substring(LikedGenres, Genres),
        (\+ list_substring(DislikedGenres, Genres))), 
        Track_List
).

%3.1
% getArtistTracks(+ArtistName, -TrackIds, -TrackNames) 5 points
getArtistTracks(ArtisName, TracksIds, TrackNames) :-
    get_tracks_id_List(ArtisName, TracksIds),!, get_tracks_name_List(ArtisName, TrackNames),!.
      
%3.2
% albumFeatures(+AlbumId, -AlbumFeatures) 5 points
albumFeatures(AlbumId, AlbumFeatures) :-
    get_album_tracks_features(AlbumId, Tracks_Features),!, get_average(Tracks_Features, AlbumFeatures),!.

%3.3 
% artistFeatures(+ArtistName, -ArtistFeatures) 5 points
artistFeatures(ArtistName, ArtistFeatures) :-
    get_artist_tracks_features(ArtistName, Artist_Tracks_Features),!, get_average(Artist_Tracks_Features, ArtistFeatures),!.

%3.4
% trackDistance(+TrackId1, +TrackId2, -Score) 5 points
trackDistance(TrackId1, TrackId2, Score) :-
    get_track_features(TrackId1, Feature_List1),!, get_track_features(TrackId2, Feature_List2),!, list_distance(Feature_List1, Feature_List2, Score),!. 

%3.5
% albumDistance(+AlbumId1, +AlbumId2, -Score) 5 points
albumDistance(AlbumId1, AlbumId2, Score) :-
    albumFeatures(AlbumId1, Feature_List1),!, albumFeatures(AlbumId2, Feature_List2),!, list_distance(Feature_List1, Feature_List2, Score),!.

%3.6
% artistDistance(+ArtistName1, +ArtistName2, -Score) 5 points
artistDistance(ArtistName1, ArtistName2, Score) :-
    artistFeatures(ArtistName1, Feature_List1),!, artistFeatures(ArtistName2, Feature_List2),!, list_distance(Feature_List1, Feature_List2, Score),!.

%3.7
% findMostSimilarTracks(+TrackId, -SimilarIds, -SimilarNames) 10 points
findMostSimilarTracks(TrackId, SimilarIds, SimilarNames) :-
    calculate_distance_tracks(TrackId, Distance_List),!, get_top_n(Distance_List, List),!, columnN(List, 2, SimilarIds),!, columnN(List, 3, SimilarNames),!.

%3.8
% findMostSimilarAlbums(+AlbumId, -SimilarIds, -SimilarNames) 10 points
findMostSimilarAlbums(AlbumId, SimilarIds, SimilarNames) :-
    calculate_distance_albums(AlbumId, Distance_List),!,get_top_n(Distance_List, List),!, columnN(List, 2, SimilarIds),!, columnN(List, 3, SimilarNames),!.

%3.9
% findMostSimilarArtists(+ArtistName, -SimilarArtists) 10 points
findMostSimilarArtists(ArtistName, SimilarNames) :-
    calculate_distance_artists(ArtistName, Distance_List),!, get_top_n(Distance_List, List),!, columnN(List, 2, SimilarNames),!.

%3.10
% filterExplicitTracks(+TrackList, -FilteredTracks) 5 points
filterExplicitTracks(TrackList, FilteredTracks) :- findall(
    Track,
    (membership(Track, TrackList), track(Track, _, _, _, Features), Features = [Head|_], not(Head == 1)),
    FilteredTracks
).

%3.11
% getTrackGenre(+TrackId, -Genres) 5 points
getTrackGenre(TrackId, Genres) :- findall(
    Genre,
    (get_artist_names(TrackId, ArtistNames), membership(ArtistName, ArtistNames), get_artist_genres(ArtistName, ArtistGenres), membership(Genre, ArtistGenres)),
    GenresList
), list_to_set(GenresList, Genres).

%3.12
% discoverPlaylist(+LikedGenres, +DislikedGenres, +Features, +FileName, -Playlist) 30 points
discoverPlaylist(LikedGenres, DislikedGenres, Features, FileName, Playlist) :-
    create_disjoint_lists(LikedGenres, DislikedGenres, FilteredLikedGenres),!, 
    suggested_playlist(FilteredLikedGenres, DislikedGenres, TrackList),!, 
    calculate_distance_features(Features, TrackList, DistanceList),!, 
    get_top_n(DistanceList, SelectedList),!,
    seperate_dashes(SelectedList, Distance, Playlist, TrackName, ArtistName),!,
    open(FileName, write, Stream),
    writeln(Stream, Playlist), writeln(Stream, TrackName), writeln(Stream, ArtistName), writeln(Stream, Distance), close(Stream) .


