export const sortIds = (id_1, id_2) => {
  if (id_1 < id_2) {
    return [id_1, id_2]
  } else {
    return [id_2, id_1]
  }
}
