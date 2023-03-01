import React from 'react';
import {Button, TextInput, StyleSheet, View} from 'react-native';
import {RootSiblingParent} from 'react-native-root-siblings';
import Toast from 'react-native-root-toast';

const registerPost = async (email, password) => {
  return await fetch('http://10.0.2.2:8082/register', {
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

const RegisterInput = ({navigation}) => {
  const [email, onChangeEmail] = React.useState(null);
  const [password, onChangePassword] = React.useState(null);
  const [passwordRe, onChangePasswordRe] = React.useState(null);

  return (
    <RootSiblingParent>
      <View style={styles.container}>
        <TextInput
          style={styles.input}
          onChangeText={onChangeEmail}
          placeholder="Email"
          value={email}
        />
        <TextInput
          style={styles.input}
          onChangeText={onChangePassword}
          value={password}
          secureTextEntry={true}
          placeholder="Password"
        />
        <TextInput
          style={styles.input}
          onChangeText={onChangePasswordRe}
          value={passwordRe}
          secureTextEntry={true}
          placeholder="Re-enter Password"
        />
        <View style={styles.buttonContainer}>
          <Button
            onPress={async () => {
              if (password !== passwordRe) {
                let toast = Toast.show('Passwords do not match.', {
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
              } else {
                const registerResult = await registerPost(email, password);
                if (registerResult.result.code === 1010) {
                  let toast = Toast.show('Registration succeeded.', {
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

                  navigation.navigate('Home');
                } else {
                  let toast = Toast.show(
                    'Registration failed.\n\n' + registerResult.result.message,
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
                }
              }
            }}
            title="SIGN UP"
            color="#0190b6"
          />
        </View>
      </View>
    </RootSiblingParent>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    backgroundColor: '#181a1b',
    color: '#ffffff',
    padding: 20,
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

export default RegisterInput;
