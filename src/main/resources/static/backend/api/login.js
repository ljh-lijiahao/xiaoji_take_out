function loginApi(data) {
  return $axios({
    'url': '/employee/login',
    'method': 'post',
    data
  })
}

function logoutApi(username){
  return $axios({
    'url': '/employee/logout?'+username,
    'method': 'post',
  })
}
