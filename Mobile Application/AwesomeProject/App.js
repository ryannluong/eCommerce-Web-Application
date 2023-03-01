import React from 'react';
import {Button, TextInput, StyleSheet, View} from 'react-native';
import {RootSiblingParent} from 'react-native-root-siblings';
import Toast from 'react-native-root-toast';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import {NavigationContainer} from '@react-navigation/native';
import RegisterInput from './Register';
import MainScreen from './Main';
import MovieDetail from './MovieDetail';
import Search from './Search';

const loginPost = async (email, password) => {
  return await fetch('http://10.0.2.2:8082/login', {
    method: 'POST',
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      email: email,
      password: password,
    }),
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

const LoginScreen = ({navigation}) => {
  const [email, onChangeEmail] = React.useState(null);
  const [password, onChangePassword] = React.useState(null);

  return (
    <RootSiblingParent
      placement={'bottom'}
      warningColor={'#1c1e1f'}
      swipeEnabled={true}>
      <View style={styles.container}>
        <TextInput
          style={styles.input}
          onChangeText={onChangeEmail}
          value={email}
          placeholder="Email"
          placeholderTextColor={'#5b5852'}
        />
        <TextInput
          style={styles.input}
          onChangeText={onChangePassword}
          value={password}
          placeholder="Password"
          placeholderTextColor={'#5b5852'}
          secureTextEntry={true}
        />
        <View style={styles.buttonContainer}>
          <Button
            onPress={async () => {
              const result = await loginPost(email, password);
              if (result.result.code === 1020) {
                navigation.navigate('Main', {
                  accessToken: result.accessToken,
                  refreshToken: result.refreshToken,
                });
              } else if (
                result.result.code === 1022 ||
                (result.result.code >= 1000 && result.result.code <= 1003)
              ) {
                let toast = Toast.show('Invalid credentials.', {
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
              } else if (result.result.code === 1021) {
                let toast = Toast.show(
                  'Account not found. Please register first.',
                  {
                    duration: Toast.durations.SHORT,
                    position: Toast.positions.BOTTOM,
                    shadow: true,
                    animation: true,
                    hideOnPress: true,
                    delay: 0,
                  },
                );

                setTimeout(() => {
                  Toast.hide(toast);
                }, 3000);
              } else {
                let toast = Toast.show(JSON.stringify(result), {
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
            title="LOGIN"
            color="#48C9B0"
          />
        </View>
        <View style={styles.buttonContainer}>
          <Button
            onPress={() => navigation.navigate('Register')}
            title="SIGN UP"
            color="#5DADE2"
          />
        </View>
      </View>
    </RootSiblingParent>
  );
};

const Stack = createNativeStackNavigator();

// make navigation bar background color #021e34
const MyStack = ({navigation}) => {
  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen
          name="Home"
          component={LoginScreen}
          options={{
            title: 'ZotMovie',
            headerStyle: {backgroundColor: '#021e34'},
            headerTitleStyle: {color: '#e4e2e0'},
            headerTintColor: '#e4e2e0',
          }}
        />
        <Stack.Screen
          name="Register"
          component={RegisterInput}
          options={{
            headerStyle: {backgroundColor: '#021e34'},
            headerTitleStyle: {color: '#e4e2e0'},
            headerTintColor: '#e4e2e0',
          }}
        />
        <Stack.Screen
          name="Main"
          component={MainScreen}
          options={{
            title: 'Movies',
            headerStyle: {backgroundColor: '#021e34'},
            headerTitleStyle: {color: '#e4e2e0'},
            headerTintColor: '#e4e2e0',
          }}
        />
        <Stack.Screen
          name="Search"
          component={Search}
          options={{
            headerStyle: {backgroundColor: '#021e34'},
            headerTitleStyle: {color: '#e4e2e0'},
            headerTintColor: '#e4e2e0',
          }}
        />
        <Stack.Screen
          name="MovieDetail"
          component={MovieDetail}
          options={({route}) => ({
            title: route.params.name,
            headerStyle: {backgroundColor: '#021e34'},
            headerTitleStyle: {color: '#e4e2e0'},
            headerTintColor: '#e4e2e0',
          })}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    backgroundColor: '#181a1b',
    color: '#ffffff',
  },
  buttonContainer: {
    margin: 20,
  },
  alternativeLayoutButtonContainer: {
    margin: 20,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  input: {
    height: 40,
    margin: 12,
    borderWidth: 1,
    padding: 10,
    backgroundColor: '#2b2a33',
    color: '#89888b',
  },
});

export default MyStack;
