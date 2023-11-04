
    $('.table .delete').on('click',function(event){

        event.preventDefault();

        var href=$(this).attr('href');

        $.get(href, function(field){

            // $('#editname').val(biz_user.name);
            console.log('field id: '+field.fieldId)
            $('#delete_id').val(field.fieldId);
        });

        $('#deleteColumnModal').modal();
    });

