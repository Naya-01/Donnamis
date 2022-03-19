const Swal = require('sweetalert2')

class Notification {
  getNotification(position = "bottom") {
    return Swal.mixin({
      toast: true,
      position: position,
      showConfirmButton: false,
      timer: 5000,
      timerProgressBar: true,
      didOpen: (toast) => {
        toast.addEventListener('mouseenter', Swal.stopTimer)
        toast.addEventListener('mouseleave', Swal.resumeTimer)
      }
    })
  }
}

export default Notification;