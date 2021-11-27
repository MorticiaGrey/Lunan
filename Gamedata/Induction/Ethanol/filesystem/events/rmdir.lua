local message = ""
if lunan.removeChild(args[4]) then
    message = "Successful";
    print(args[4] .. ": Successfully deleted")
else
    message = "Unsuccessful";
    print(args[4] .. ": Attempted deletion blocked")
end
lunan.sendPacket("print", args[1], {message})
