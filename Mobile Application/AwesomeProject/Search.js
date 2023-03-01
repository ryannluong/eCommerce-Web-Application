import React from 'react';
import {
  Text,
  StyleSheet,
  View,
  TextInput,
  Button,
  ScrollView,
  SafeAreaView,
} from 'react-native';
import {Picker} from '@react-native-picker/picker';
import {RootSiblingParent} from 'react-native-root-siblings';
import Toast from 'react-native-root-toast';

const getSearch = async payload => {
  const data = {
    title: payload.title,
    year: payload.year,
    director: payload.director,
    genre: payload.genre,
    limit: payload.limit,
    page: payload.page,
    orderBy: payload.orderBy,
    direction: payload.direction,
  };

  const urlParams = () => {
    let params = '';
    for (let key in data) {
      if (data[key] !== '') {
        params += `&${key}=${data[key]}`;
      }
    }
    return params;
  };

  const q = urlParams();

  return await fetch(`http://10.0.2.2:8083/movie/search?${q}`, {
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
  // const queryParams = {
  //   title: payload.title,
  //   director: payload.director,
  //   genre: payload.genre,
  //   orderBy: payload.orderBy,
  //   direction: payload.direction,
  //   year: payload.year,
  //   page: payload.page,
  //   limit: payload.limit,
  // };
  //
  // const options = {
  //   method: 'GET',
  //   baseURL: 'http://localhost:8083',
  //   url: '/movie/search',
  //   headers: {
  //     Authorization: 'Bearer ' + payload.accessToken,
  //   },
  //   params: queryParams,
  // };
  //
  // return Axios.request(options)
  //   .then(response => response.json())
  //   .then(json => {
  //     console.log('In Search: ' + JSON.stringify(json));
  //     return json;
  //   })
  //   .catch(error => {
  //     console.error(error);
  //   });
};

const Search = ({route, navigation}) => {
  const {accessToken, refreshToken} = route.params;
  const [title, setTitle] = React.useState('');
  const [year, setYear] = React.useState('');
  const [director, setDirector] = React.useState('');
  const [genre, setGenre] = React.useState('');
  const [limit, setLimit] = React.useState(10);
  const [direction, setDirection] = React.useState('asc');
  const [orderBy, setOrderBy] = React.useState('title');
  const [page, setPage] = React.useState(1);

  // Create search bar and buttons
  return (
    <RootSiblingParent>
      <SafeAreaView style={styles.container}>
        <ScrollView style={styles.scrollView} alwaysBounceVertical={true}>
          <View style={styles.baseText}>
            <TextInput
              placeholder={'Title'}
              placeholderTextColor={'#5b5852'}
              key="titleForm"
              style={styles.input}
              onChangeText={setTitle}
              value={title}
            />
          </View>

          <View style={styles.baseText}>
            <TextInput
              placeholder={'Year'}
              placeholderTextColor={'#5b5852'}
              key="yearForm"
              style={styles.input}
              onChangeText={setYear}
              value={year}
            />
          </View>

          <View style={styles.baseText}>
            <TextInput
              placeholder={'Director'}
              placeholderTextColor={'#5b5852'}
              key="directorForm"
              style={styles.input}
              onChangeText={setDirector}
              value={director}
            />
          </View>

          <View style={styles.baseText}>
            <TextInput
              placeholder={'Genre'}
              placeholderTextColor={'#5b5852'}
              key="genreForm"
              style={styles.input}
              onChangeText={setGenre}
              value={genre}
            />
          </View>

          <View style={styles.baseText}>
            <Text style={styles.baseText}>{'\nLimit'}</Text>
            <Picker
              key="limitForm"
              selectedValue={limit}
              onValueChange={(itemValue, itemIndex) => setLimit(itemValue)}
              style={styles.dropdown}>
              <Picker.Item label="10" value={10} />
              <Picker.Item label="25" value={25} />
              <Picker.Item label="50" value={50} />
              <Picker.Item label="100" value={100} />
            </Picker>
          </View>

          <View style={styles.baseText}>
            <Text style={styles.baseText}>{'\nOrder By'}</Text>
            <Picker
              key="orderByForm"
              selectedValue={orderBy}
              onValueChange={(itemValue, itemIndex) => setOrderBy(itemValue)}
              style={styles.dropdown}>
              <Picker.Item label="Title" value="title" />
              <Picker.Item label="Rating" value="rating" />
              <Picker.Item label="Year" value="year" />
            </Picker>
          </View>

          <View style={styles.baseText}>
            <Text style={styles.baseText}>{'\nDirection'}</Text>
            <Picker
              key="directionForm"
              selectedValue={direction}
              onValueChange={(itemValue, itemIndex) => setDirection(itemValue)}
              style={styles.dropdown}>
              <Picker.Item label="Ascending" value="asc" />
              <Picker.Item label="Descending" value="desc" />
            </Picker>
          </View>

          <View style={styles.buttonContainer}>
            <Button
              key="searchButton"
              title="Search"
              onPress={async () => {
                const payload = {
                  title: title,
                  year: year,
                  director: director,
                  genre: genre,
                  limit: limit,
                  orderBy: orderBy,
                  direction: direction,
                  page: page,
                  accessToken: accessToken,
                };

                console.log('Payload: ', payload);
                const result = await getSearch(payload);
                if (result.result.code === 2020) {
                  navigation.navigate('Main', {
                    accessToken: accessToken,
                    refreshToken: refreshToken,
                    result: result.movies,
                  });
                } else {
                  let toast = Toast.show(result.result.message, {
                    duration: Toast.durations.SHORT,
                    position: Toast.positions.BOTTOM,
                    shadow: true,
                    animation: true,
                    hideOnPress: true,
                    delay: 0,
                  });

                  setTimeout(() => {
                    Toast.hide(toast);
                  }, 3000);
                }
              }}
            />
          </View>
        </ScrollView>

        {/*<View style={styles.baseText}>*/}
        {/*  <Button*/}
        {/*    key="clearButton"*/}
        {/*    title="Clear"*/}
        {/*    onPress={() => {*/}
        {/*      document.getElementById('titleForm').value = '';*/}
        {/*      document.getElementById('yearForm').value = '';*/}
        {/*      document.getElementById('directorForm').value = '';*/}
        {/*      document.getElementById('genreForm').value = '';*/}
        {/*      document.getElementById('limitForm').value = '10';*/}
        {/*      document.getElementById('orderByForm').value = 'title';*/}
        {/*      document.getElementById('directionForm').value = 'asc';*/}

        {/*      setTitle('');*/}
        {/*      setYear('');*/}
        {/*      setDirector('');*/}
        {/*      setGenre('');*/}
        {/*      setLimit(10);*/}
        {/*      setOrderBy('title');*/}
        {/*      setDirection('asc');*/}
        {/*    }}*/}
        {/*  />*/}
        {/*</View>*/}
      </SafeAreaView>
    </RootSiblingParent>
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
    backgroundColor: '#181a1b',
    color: '#89888b',
    borderColor: '#3c4144',
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
  dropdown: {
    borderWidth: 1,
    padding: 10,
    margin: 12,
    height: 40,
    backgroundColor: '#2b2a33',
    color: '#89888b',
  },
});

export default Search;
