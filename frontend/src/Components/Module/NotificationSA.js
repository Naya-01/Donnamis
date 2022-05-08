const Swal = require('sweetalert2')

class NotificationSA {

  /**
   * Create a popup ready to be used using Swal.
   *
   * @param position : the position of the notification
   */
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

export default NotificationSA;