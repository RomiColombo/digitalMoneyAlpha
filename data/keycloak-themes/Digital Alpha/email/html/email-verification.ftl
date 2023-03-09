<#import "template.ftl" as layout>
<@layout.emailLayout>
    <div style="background-color: #A6A6A6;width: 100%;height: 830px;padding: 30px 0;">
            <table style="padding: 20px;margin:auto;width: 480px;height: 730px; background: #32353C;border: 1px solid #000000;boxShadow: 0 4px 4px rgba(0, 0, 0, 0.25);border-radius: 20px;">
                <tbody>
                    <tr>
                        <td style="height:100px;background-repeat:no-repeat;background-size:contain;background-image:url(https://res.cloudinary.com/sagarciaescobar/image/upload/v1669491288/digital-alpha/digitalAlpha-white.png);background-position:center center;" 
                        background="https://res.cloudinary.com/sagarciaescobar/image/upload/v1669491288/digital-alpha/digitalAlpha-white.png">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p style="font-family: 'Roboto';font-style: normal;font-weight: 400;font-size: 18px;line-height: 140.62%;text-align: center;color: #FFFFFF;">
                                Alguien ha creado una cuenta de con esta dirección de email.Si has sido tú, haz click en el enlace siguiente para verificar tu dirección de email.
                            <p>
                        </td>
                    </tr>
                    <tr>
                        <td style="height:225px;background-repeat:no-repeat;background-size:contain;background-image:url(https://res.cloudinary.com/sagarciaescobar/image/upload/c_scale,h_876/c_crop,h_820,x_30,y_20/v1669483978/digital-alpha/DigitalAlpha-card.png);background-position:center center;" background="https://res.cloudinary.com/sagarciaescobar/image/upload/c_scale,h_876/c_crop,h_820,x_30,y_20/v1669483978/digital-alpha/DigitalAlpha-card.png">
                        </td>
                    </tr>
                    <tr>    
                        <td>
                            <p style="font-family: 'Roboto';font-style: normal;font-weight: 400;font-size: 12px;line-height: 140.62%;text-align: center;color: #FFFFFF;">Este link sera invalido en ${msg(linkExpiration)} minutos</p>
                            <a style="display:block;width: 100%;height: 46px;background: rgba(204, 237, 0, 0.67);border-radius: 13px;text-decoration:none;font-family: 'Roboto';font-style: normal;font-weight: 600;font-size: 15px;line-height: 300%;text-align: center;color: #FFFFFF;" href="${msg(link)}">Link de verficiacion de correo</a>
                            <p style="padding-top:20px;font-family: 'Roboto';font-style: normal;font-weight: 400;font-size: 12px;line-height: 140.62%;text-align: center;color: #FFFFFF;">Si tú no has creado esta cuenta, simplemente ignora este mensaje.</p>
                        </td>
                    </tr>
                </tbody>
            </table>
    <div>
</@layout.emailLayout>
