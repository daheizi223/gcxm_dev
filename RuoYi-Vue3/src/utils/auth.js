import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function clearAuth() {
  // 清除token
  removeToken()
  // 清除localStorage中的用户信息
  localStorage.removeItem('userInfo')
  localStorage.removeItem('roles')
  localStorage.removeItem('permissions')
  // 清除sessionStorage
  sessionStorage.clear()
}
