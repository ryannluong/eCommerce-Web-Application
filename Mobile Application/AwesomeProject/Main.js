import React from 'react';
import {
  Text,
  FlatList,
  StyleSheet,
  View,
  TouchableHighlight,
  Image,
  Button,
  LayoutAnimation,
} from 'react-native';
import ActionButton from 'react-native-action-button';
import Icon from 'react-native-vector-icons/Ionicons';

const movieDivider = () => {
  return (
    <View
      style={{
        height: 1,
        width: '100%',
        backgroundColor: '#607D8B',
      }}
    />
  );
};

const getSearch = async payload => {
  return await fetch('http://10.0.2.2:8083/movie/search', {
    method: 'GET',
    headers: {
      Authorization: 'Bearer ' + payload.accessToken,
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
  })
    .then(response => response.json())
    .then(json => {
      console.log('In Search: ' + JSON.stringify(json));
      return json;
    })
    .catch(error => {
      console.error(error);
    });
};

const MainScreen = ({route, navigation}) => {
  let {accessToken, refreshToken, result} = route.params;
  let [state, setState] = React.useState({isActionButtonVisible: true});
  let [defaultResult, setDefaultResult] = React.useState([]);
  let _listViewOffset = 0;
  // console.log('Result: ' + JSON.stringify(result));

  React.useEffect(() => {
    async function fetchData() {
      let response = await getSearch({
        accessToken: accessToken,
      });

      return response;
    }
    fetchData().then(r => {
      if (r.result.code === 2020) {
        setDefaultResult(r.movies);
        console.log('Fetched default result.');
        // console.log('Default result: ' + JSON.stringify(defaultResult));
      }
    });
  }, []);

  const _onScroll = event => {
    const CustomLayoutLinear = {
      duration: 100,
      create: {
        type: LayoutAnimation.Types.linear,
        property: LayoutAnimation.Properties.opacity,
      },
      update: {
        type: LayoutAnimation.Types.linear,
        property: LayoutAnimation.Properties.opacity,
      },
      delete: {
        type: LayoutAnimation.Types.linear,
        property: LayoutAnimation.Properties.opacity,
      },
    };
    // Check if the user is scrolling up or down by confronting the new scroll position with your own one
    const currentOffset = event.nativeEvent.contentOffset.y;
    const direction =
      currentOffset > 0 && currentOffset > _listViewOffset ? 'down' : 'up';
    // If the user is scrolling down (and the action-button is still visible) hide it
    const isActionButtonVisible = direction === 'up';
    if (isActionButtonVisible !== state.isActionButtonVisible) {
      LayoutAnimation.configureNext(CustomLayoutLinear);
      setState({isActionButtonVisible: isActionButtonVisible});
    }
    // Update your scroll position
    _listViewOffset = currentOffset;
  };

  return (
    <View style={styles.container}>
      {
        <FlatList
          onScroll={_onScroll}
          data={result ? result : defaultResult}
          keyExtractor={item => item.id}
          renderItem={({item}) => (
            <View style={styles.container}>
              <TouchableHighlight
                onPress={() => {
                  navigation.navigate('MovieDetail', {
                    accessToken: accessToken,
                    refreshToken: refreshToken,
                    movieId: item.id,
                    name: item.title,
                  });
                }}
                underlayColor="white">
                <View style={styles.subContainer} flexDirection="row">
                  <Image
                    style={styles.thumbnail}
                    source={{
                      uri: 'https://image.tmdb.org/t/p/w500' + item.posterPath,
                    }}
                  />
                  <Text style={{fontSize: 14, margin: 10, color: '#e8e6e3'}}>
                    {item.title +
                      ' • ' +
                      item.year +
                      ' • ★' +
                      item.rating +
                      '\nDirector: ' +
                      item.director}
                  </Text>
                </View>
              </TouchableHighlight>
            </View>
          )}
          ItemSeparatorComponent={movieDivider}
        />
      }

      {/*<Button*/}
      {/*  onPress={() =>*/}
      {/*    navigation.navigate('Search', {*/}
      {/*      accessToken: accessToken,*/}
      {/*      refreshToken: refreshToken,*/}
      {/*    })*/}
      {/*  }*/}
      {/*  title="Go to search"*/}
      {/*/>*/}
      {state.isActionButtonVisible ? (
        <ActionButton
          buttonColor={'#01b4e4'}
          renderIcon={() => (
            <Icon name={'search-outline'} style={styles.actionButtonIcon} />
          )}
          onPress={() =>
            navigation.navigate('Search', {
              accessToken: accessToken,
              refreshToken: refreshToken,
            })
          }
        />
      ) : null}
    </View>
  );
};

const styles = StyleSheet.create({
  view: {
    margin: 10,
  },
  container: {
    flex: 1,
    justifyContent: 'center',
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
  thumbnail: {
    width: 50,
    height: 50,
    margin: 10,
  },
  actionButtonIcon: {
    fontSize: 20,
    height: 22,
    color: '#e4e2e0',
  },
});

export default MainScreen;
