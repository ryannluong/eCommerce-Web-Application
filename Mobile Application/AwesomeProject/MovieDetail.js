import React from 'react';
import {
  Text,
  FlatList,
  StyleSheet,
  View,
  Image,
  SafeAreaView,
  ScrollView,
} from 'react-native';

const movieById = async (id, accessToken) => {
  return await fetch('http://10.0.2.2:8083/movie/' + id, {
    method: 'GET',
    headers: {
      Authorization: 'Bearer ' + accessToken,
    },
  })
    .then(response => response.json())
    .then(json => {
      console.log(json);
      return json;
    })
    .catch(error => {
      console.error(error);
    });
};

const MovieDetail = ({route, navigation}) => {
  const {accessToken, refreshToken, movieId, name} = route.params;
  const [movie, setMovie] = React.useState([]);
  const [persons, setPersons] = React.useState([]);
  const [genres, setGenres] = React.useState([]);

  const getMovie = async () => {
    const result = await movieById(movieId, accessToken);
    setMovie(result.movie);
    setPersons(result.persons);
    setGenres(result.genres);

    console.log('movie: ' + JSON.stringify(result.movie));
    console.log('persons: ' + JSON.stringify(result.persons));
    console.log('genres: ' + JSON.stringify(result.genres));
  };

  React.useEffect(() => {
    getMovie().then();
  }, []);

  // console.log('Post: ' + post);

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.scrollView} alwaysBounceVertical={true}>
        <View style={styles.container}>
          <Image
            style={styles.thumbnail}
            source={{
              uri: 'https://image.tmdb.org/t/p/w500' + movie.posterPath,
            }}
          />
          <Text style={styles.titleText}>{movie.title}</Text>
          <Text style={styles.baseText}>
            {movie.year +
              ' | ★' +
              movie.rating +
              ' | ' +
              movie.numVotes +
              ' ratings'}
          </Text>
          <Text style={styles.baseText}>
            {genres.map(genre => {
              return genre.name + '  ';
            })}
          </Text>
          <Text style={styles.subtitleText}>{'\nOverview'}</Text>
          <Text style={styles.baseText}>{movie.overview}</Text>
          <Text style={styles.subtitleText}>{'\nCast'}</Text>
          <Text style={styles.baseText}>
            {persons.map(person => {
              return person.name + '\n';
            })}
          </Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

// text color is #ffffff
const styles = StyleSheet.create({
  view: {
    margin: 10,
  },
  container: {
    flex: 1,
    justifyContent: 'center',
    backgroundColor: '#181a1b',
    color: '#ffffff',
    padding: 20,
  },
  scrollView: {
    flex: 1,
    backgroundColor: '#181a1b',
    color: '#ffffff',
  },
  subContainer: {
    flex: 1,
    backgroundColor: '#181a1b',
    color: '#ffffff',
  },
  buttonContainer: {
    margin: 20,
  },
  input: {
    height: 40,
    margin: 12,
    borderWidth: 1,
    padding: 10,
    backgroundColor: '#2b2a33',
    color: '#89888b',
  },
  baseText: {
    fontFamily: 'Cochin',
    color: '#e8e6e3',
  },
  titleText: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#e8e6e3',
  },
  subtitleText: {
    fontSize: 18,
    color: '#e8e6e3',
    fontWeight: 'bold',
  },
  thumbnail: {
    width: 175,
    height: 263,
    margin: 10,
  },
});

export default MovieDetail;
